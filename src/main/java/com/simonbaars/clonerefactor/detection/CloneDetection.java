package com.simonbaars.clonerefactor.detection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneDetection {
	private static final int MIN_AMOUNT_OF_LINES = 3;
	private static final int MIN_AMOUNT_OF_TOKENS = 15;

	public CloneDetection() {

	}

	public List<Sequence> findChains(Location lastLoc) {
		Sequence buildingChains = new Sequence();
		final Set<Location> visitedLocations = new HashSet<>();
		final List<Sequence> clones = new ArrayList<Sequence>();
		for(;lastLoc!=null;lastLoc = lastLoc.getPrevLine()) {
			Sequence newClones = collectClones(lastLoc);
			visitedLocations.addAll(newClones.getSequence().subList(1, newClones.size()));
			if(newClones.size()>=1)
				buildingChains = makeValid(lastLoc, buildingChains, newClones, clones, visitedLocations); //Because of the recent additions the current sequence may be invalidated
			if(lastLoc.getPrevLine()!=null) {
				if(lastLoc.getPrevLine().getFile()!=lastLoc.getFile()) {
					buildingChains.getSequence().clear();
				}
			}
		}
		return clones;
	}


	private Sequence makeValid(Location lastLoc, Sequence oldClones, Sequence newClones, List<Sequence> clones, Set<Location> visitedLocations) {
		Map<Location /*oldClones*/, Location /*newClones*/> validChains = oldClones.getSequence().stream().distinct().filter(e -> newClones.getSequence().contains(e.getPrevLine())).collect(Collectors.toMap(e -> e, e -> e.getPrevLine()));

		if(validChains.size()!=oldClones.size() && !oldClones.getSequence().isEmpty()) {
			checkValidClones(oldClones, oldClones.getSequence().stream().filter(e -> !newClones.getSequence().contains(e.getPrevLine())).collect(Collectors.toList()), clones);
		}

		for(Entry<Location, Location> validChain : validChains.entrySet()) {
			Location newClone = validChain.getValue();
			Location oldClone = validChain.getKey();
			newClone.mergeWith(oldClone);
		}

		if(visitedLocations.contains(lastLoc)){
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
					if(loc1.getRange().contains(loc2.getRange())) {
						newClones.getSequence().remove(j);
						j--;
					} else if(loc2.getRange().contains(loc1.getRange())) {
						newClones.getSequence().remove(i);
						i--;
						continue outerloop;
					}
				}
			}
		}
		return newClones;
	}

	private void checkValidClones(Sequence oldClones, List<Location> endedClones, List<Sequence> clones) {
		ListMap<Integer /*Sequence size*/, Location /* Clones */> cloneList = new ListMap<>();
		endedClones.stream().filter(e -> e.getAmountOfLines() >= MIN_AMOUNT_OF_LINES).forEach(e -> cloneList.addTo(e.getAmountOfLines(), e));
		for(List<Location> l : cloneList.values()) {
			if(l.stream().anyMatch(e -> endedClones.contains(e))) {
				int origEl = l.size();
				for(Location l2 : oldClones.getSequence()) {
					if(l.get(0)!= l2 && l2.getAmountOfLines()>=l.get(0).getAmountOfLines()) {
						l.add(new Location(l2));
					}
				}
				IntStream.range(0, origEl).forEach(i -> l.set(i, new Location(l.get(i))));
				if(l.stream().collect(Collectors.summingInt(e -> e.getAmountOfTokens())) > MIN_AMOUNT_OF_TOKENS && l.size()>1) {
					Sequence newSequence = new Sequence(l);
					removeDuplicatesOf(clones, newSequence);
					clones.add(newSequence);
				}
				continue;
			}
		}
	}
	
	public void removeDuplicatesOf(List<Sequence> clones, Sequence l) {
		for(int i = clones.size()-1; i>=0; i--) {
			Sequence clone = clones.get(i);
			if(clone.getSequence().get(0).getFile() != l.getSequence().get(0).getFile()) //Limit our searching scope for performance purposes
				break;
			if(isSubset(clones.get(i), l))
				clones.remove(i);
		}
	}
	
	private boolean isSubset(Sequence sequence, Sequence subsetOf) {
		return sequence.getSequence().size() == subsetOf.size() && subsetOf.getSequence().stream().allMatch(e -> sequence.getSequence().stream().anyMatch(f -> e.getRange().contains(f.getRange())));
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

