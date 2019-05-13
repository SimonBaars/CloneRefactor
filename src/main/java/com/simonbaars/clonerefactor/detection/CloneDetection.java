package com.simonbaars.clonerefactor.detection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.Settings;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class CloneDetection {
	final List<Sequence> clones = new ArrayList<>();

	public CloneDetection() {}

	public List<Sequence> findChains(Location lastLoc) {
		for(Sequence buildingChains = new Sequence();lastLoc!=null;lastLoc = lastLoc.getPrevLine()) {
			Sequence newClones = collectClones(lastLoc);
			if(!buildingChains.getSequence().isEmpty() || newClones.size()>1)
				buildingChains = makeValid(lastLoc, buildingChains, newClones); //Because of the recent additions the current sequence may be invalidated
			if(buildingChains.size() == 1 || (lastLoc.getPrevLine()!=null && lastLoc.getPrevLine().getFile()!=lastLoc.getFile()))
				buildingChains.getSequence().clear();
		}
		return clones;
	}


	private Sequence makeValid(Location lastLoc, Sequence oldClones, Sequence newClones) {
		Map<Location /*oldClones*/, Location /*newClones*/> validChains = oldClones.getSequence().stream().distinct().filter(oldClone -> 
			newClones.getSequence().stream().anyMatch(newClone -> newClone.getFile() == oldClone.getFile() && oldClone.getPrevLine()!=null && oldClone.getFile() == oldClone.getPrevLine().getFile()
		    && newClone.getContents().getRange()!= null && newClone.getContents().getRange().equals(oldClone.getPrevLine().getContents().getRange()))).collect(Collectors.toMap(e -> e, e -> e.getPrevLine()));
		
		if(validChains.size()!=oldClones.size() && !oldClones.getSequence().isEmpty()) {
			checkValidClones(oldClones, oldClones.getSequence().stream().filter(e -> !newClones.getSequence().contains(e.getPrevLine())).collect(Collectors.toList()));
		}

		for(Entry<Location, Location> validChain : validChains.entrySet()) {
			Location l = validChain.getValue().mergeWith(validChain.getKey());
			newClones.getSequence().set(newClones.getSequence().indexOf(validChain.getValue()), l);
			validChain.setValue(l);
		}
		
		if(lastLoc.getPrevLine()==null || lastLoc.getPrevLine().getFile()!=lastLoc.getFile()) {
			checkValidClones(newClones, newClones.getSequence());
		}
		
		return newClones;
	}

	private void checkValidClones(Sequence oldClones, List<Location> endedClones) {
		ListMap<Integer /*Sequence size*/, Location /* Clones */> cloneList = new ListMap<>();
		endedClones.stream().filter(this::checkNodesThreshold).forEach(e -> cloneList.addTo(e.getAmountOfNodes(), e));
		for(Entry<Integer, List<Location>> entry : cloneList.entrySet()) {
			int amountOfNodes = entry.getKey();
			List<Location> l = entry.getValue();
			addAllNonEndedLocations(oldClones, amountOfNodes, l);
			createClone(l);
			continue;
		}
	}

	private boolean checkNodesThreshold(Location e) {
		return e.getAmountOfNodes() >= Settings.get().getMinAmountOfNodes();
	}

	private void createClone(List<Location> l) {
		if(checkTokenThreshold(l) && checkLinesThreshold(l) && l.size()>1) {
			Sequence newSequence = new Sequence(l);
			if(removeDuplicatesOf(newSequence))
				clones.add(newSequence);
		}
	}

	private void addAllNonEndedLocations(Sequence oldClones, int amountOfNodes, List<Location> l) {
		for(Location l2 : oldClones.getSequence()) {
			if(!l.contains(l2) && l2.getAmountOfNodes()>=amountOfNodes) {
				l.add(new Location(l2, getRange(l2, l.get(0)), amountOfNodes, l.get(0).getContents().getCompare().size()));
			}
		}
	}

	private boolean checkLinesThreshold(List<Location> l) {
		return l.stream().collect(Collectors.summingInt(e -> e.getAmountOfLines())) > Settings.get().getMinAmountOfLines();
	}

	private boolean checkTokenThreshold(List<Location> l) {
		return l.stream().collect(Collectors.summingInt(e -> e.getAmountOfTokens())) > Settings.get().getMinAmountOfTokens();
	}
	
	private Range getRange(Location l2, Location location) {
		return l2.getRange().withEnd(backtrace(l2, location.getAmountOfNodes()));
	}

	private Position backtrace(Location l2, int amountOfNodes) {
		for(int i = 1; i<amountOfNodes; i++)
			l2 = l2.getNextLine();
		return l2.getContents().getRange().end;
	}

	public boolean removeDuplicatesOf(Sequence l) {
		clones.removeIf(e -> isSubset(e, l));
		l.getSequence().removeIf(e -> l.getSequence().stream().anyMatch(f -> f!=e && f.getFile() == e.getFile() && f.getRange().contains(e.getRange())));
		return !clones.stream().anyMatch(e -> isSubset(l, e));
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

