package com.simonbaars.clonerefactor.detection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneDetection {
	private static final int MIN_AMOUNT_OF_LINES = 6;
	private static final int MIN_AMOUNT_OF_TOKENS = 15;

	public CloneDetection() {

	}

	public List<Sequence> findChains(Location lastLoc, ListMap<Integer, Location> clonereg) {
		Sequence buildingChains = new Sequence();
		final List<Sequence> clones = new ArrayList<Sequence>();
		for(;lastLoc!=null;lastLoc = lastLoc.getPrevLine()) {
			Sequence newClones = collectClones(lastLoc);
			if(newClones.size()>=1)
				buildingChains = makeValid(lastLoc, buildingChains, newClones, clones, clonereg); //Because of the recent additions the current sequence may be invalidated
			if(lastLoc.getPrevLine()!=null) {
				if(lastLoc.getPrevLine().getFile()!=lastLoc.getFile()) {
					buildingChains.getSequence().clear();
				}
			}
		}
		return clones;
	}


	private Sequence makeValid(Location lastLoc, Sequence oldClones, Sequence newClones, List<Sequence> clones, ListMap<Integer, Location> clonereg) {
		Map<Location /*oldClones*/, Location /*newClones*/> validChains = oldClones.getSequence().stream().distinct().filter(e -> newClones.getSequence().contains(e.getPrevLine())).collect(Collectors.toMap(e -> e, e -> e.getPrevLine()));

		if(validChains.size()!=oldClones.size() && !oldClones.getSequence().isEmpty()) {
			checkValidClones(oldClones, oldClones.getSequence().stream().filter(e -> !newClones.getSequence().contains(e.getPrevLine())).collect(Collectors.toList()), clones);
		}

		for(Entry<Location, Location> validChain : validChains.entrySet()) {
			Location newClone = validChain.getValue();
			Location oldClone = validChain.getKey();
			newClone.setEndLine(oldClone.getEndLine());
			newClone.setAmountOfLines(oldClone.getAmountOfLines()+1);
			newClone.setAmountOfTokens(oldClone.getAmountOfTokens()+newClone.getAmountOfTokens());
		}

		if(lastLoc.isLocationParsed(clonereg)){
			newClones.getSequence().clear();
			newClones.getSequence().addAll(validChains.values());
		}
		mergeClones(newClones);

		return newClones;
	}

	private  Sequence mergeClones(Sequence newClones) {
		outerloop: for(int i = 0; i<newClones.size(); i++) {
			Location loc1 = newClones.getSequence().get(i);
			for(int j = i+1; j<newClones.size(); j++) {
				Location loc2 = newClones.getSequence().get(j);
				if(loc1.getFile() == loc2.getFile()) {
					int beginLineOne = loc1.getBeginLine()+1;
					int endLineOne = loc1.getEndLine()+1;
					int beginLineTwo = loc2.getBeginLine();
					int endLineTwo = loc2.getEndLine();
					if(overlap(beginLineOne, endLineOne, beginLineTwo, endLineTwo)) {
						if(endLineOne>endLineTwo) {
							newClones.getSequence().remove(j);
							j--;
						} else {
							newClones.getSequence().remove(i);
							i--;
							continue outerloop;
						}
					}
				}
			}
		}
	return newClones;
	}

	public boolean overlap(int x1, int y1, int x2, int y2) {
		return x1 <= y2 || y1 <= x2;
	}

	private void checkValidClones(Sequence oldClones, List<Location> endedClones, List<Sequence> clones) {
		ListMap<Integer /*Sequence size*/, Location /* Clones */> cloneList = new ListMap<>();
		endedClones.stream().filter(e -> e.getAmountOfLines() >= MIN_AMOUNT_OF_LINES).forEach(e -> cloneList.addTo(e.getAmountOfLines(), e));
		for(List<Location> l : cloneList.values()) {
			if(l.stream().anyMatch(e -> endedClones.contains(e))) {
				int origEl = l.size();
				for(Location l2 : oldClones.getSequence()) {
					if(l.get(0)!= l2 && l2.getAmountOfLines()>=l.get(0).getAmountOfLines()) {
						l.add(new Location(l2.getFile(), l2.getBeginLine(), findActualEndLine(l2, l.get(0).getAmountOfLines()), l.get(0).getAmountOfLines(), l.get(0).getAmountOfTokens()));
					}
				}
				IntStream.range(0, origEl).forEach(i -> l.set(i, new Location(l.get(i).getFile(), l.get(i).getBeginLine(), l.get(i).getEndLine(), l.get(i).getAmountOfLines(), l.get(i).getAmountOfTokens())));
				if(l.stream().collect(Collectors.summingInt(e -> e.getAmountOfTokens())) > MIN_AMOUNT_OF_TOKENS && l.size()>1)
					clones.add(new Sequence(l));
				continue;
			}
		}
	}

	private int findActualEndLine(Location l2, int amountOfLines) {
		if(amountOfLines>1 && l2.getNextLine()!=null && l2.getNextLine().getFile() == l2.getFile())
			return findActualEndLine(l2.getNextLine(), amountOfLines-1);
		return l2.getBeginLine();
	}

	private Sequence collectClones(Location lastLoc) {
		Sequence c = new Sequence();
		while(lastLoc!=null) {
			c.add(lastLoc);
			lastLoc = lastLoc.getClone();
		}
		return c;
	}
}

