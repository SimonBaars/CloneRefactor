package com.simonbaars.clonerefactor.detection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneDetection {
	private static final int MIN_AMOUNT_OF_LINES = 6;
	private static final int MIN_AMOUNT_OF_TOKENS = 10;
	private static final int MIN_AMOUNT_OF_NODES = 6; 
	final Set<Location> visitedLocations = new HashSet<>();
	final List<Sequence> clones = new ArrayList<>();

	public CloneDetection() {}

	public List<Sequence> findChains(Location lastLoc) {
		for(Sequence buildingChains = new Sequence();lastLoc!=null;lastLoc = lastLoc.getPrevLine()) {
			Sequence newClones = collectClones(lastLoc);
			visitedLocations.addAll(newClones.getSequence().subList(1, newClones.size()));
			if(!buildingChains.getSequence().isEmpty() || newClones.size()>1)
				buildingChains = makeValid(lastLoc, buildingChains, newClones); //Because of the recent additions the current sequence may be invalidated
			if(lastLoc.getPrevLine()!=null && lastLoc.getPrevLine().getFile()!=lastLoc.getFile())
				buildingChains.getSequence().clear();
		}
		return clones;
	}


	private Sequence makeValid(Location lastLoc, Sequence oldClones, Sequence newClones) {
		Map<Location /*oldClones*/, Location /*newClones*/> validChains = oldClones.getSequence().stream().distinct().filter(oldClone -> newClones.getSequence().stream().anyMatch(newClone -> newClone == oldClone.getPrevLine() && newClone.getFile() == oldClone.getFile())).collect(Collectors.toMap(e -> e, e -> e.getPrevLine()));

		if(validChains.size()!=oldClones.size() && !oldClones.getSequence().isEmpty()) {
			checkValidClones(oldClones, oldClones.getSequence().stream().filter(e -> !newClones.getSequence().contains(e.getPrevLine())).collect(Collectors.toList()));
		}

		for(Entry<Location, Location> validChain : validChains.entrySet()) {
			validChain.getValue().mergeWith(validChain.getKey());
		}

		if(visitedLocations.contains(lastLoc)){
			newClones.getSequence().clear();
			newClones.getSequence().addAll(validChains.values());
		}

		return newClones;
	}

	private void checkValidClones(Sequence oldClones, List<Location> endedClones) {
		ListMap<Integer /*Sequence size*/, Location /* Clones */> cloneList = new ListMap<>();
		endedClones.stream().filter(e -> e.getAmountOfNodes() >= MIN_AMOUNT_OF_NODES).forEach(e -> cloneList.addTo(e.getAmountOfNodes(), e));
		for(List<Location> l : cloneList.values()) {
			if(l.stream().anyMatch(e -> endedClones.contains(e))) {
				for(Location l2 : oldClones.getSequence()) {
					if(l.get(0)!= l2 && l2.getAmountOfNodes()>=l.get(0).getAmountOfNodes()) {
						l.add(new Location(l2, getRange(l2, l.get(0))));
					}
				}
				if(l.stream().collect(Collectors.summingInt(e -> e.getAmountOfTokens())) > MIN_AMOUNT_OF_TOKENS && l.stream().collect(Collectors.summingInt(e -> e.getAmountOfLines())) > MIN_AMOUNT_OF_LINES && l.size()>1) {
					Sequence newSequence = new Sequence(l);
					removeDuplicatesOf(newSequence);
					clones.add(newSequence);
				}
				continue;
			}
		}
	}
	
	private Range getRange(Location l2, Location location) {
		System.out.println("getRange "+l2+", "+location );
		return l2.getRange().withEnd(backtrace(l2, location.getAmountOfNodes()));
	}

	private Position backtrace(Location l2, int amountOfNodes) {
		System.out.println("Backtracing "+l2+" "+amountOfNodes);
		for(int i = 1; i<amountOfNodes; i++)
			l2 = l2.getNextLine();
		return l2.getContents().getRange().end;
	}

	public void removeDuplicatesOf(Sequence l) {
		clones.removeIf(e -> isSubset(e, l));
		l.getSequence().removeIf(e -> l.getSequence().stream().anyMatch(f -> f!=e && f.getFile() == e.getFile() && f.getRange().contains(e.getRange())));
	}
	
	private boolean isSubset(Sequence existentClone, Sequence newClone) {
		return existentClone.getSequence().stream().allMatch(oldLoc -> newClone.getSequence().stream().anyMatch(newLoc -> oldLoc.getFile() == newLoc.getFile() && newLoc.getRange().contains(oldLoc.getRange())));
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

