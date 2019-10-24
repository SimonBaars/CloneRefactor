package com.simonbaars.clonerefactor.detection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.datatype.map.ListMap;
import com.simonbaars.clonerefactor.detection.interfaces.ChecksThresholds;
import com.simonbaars.clonerefactor.detection.interfaces.RemovesDuplicates;
import com.simonbaars.clonerefactor.detection.metrics.ProblemType;
import com.simonbaars.clonerefactor.detection.metrics.SequenceObservable;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.graph.interfaces.DeterminesNodeTokens;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.settings.progress.Progress;

public class CloneDetection implements ChecksThresholds, RemovesDuplicates, DeterminesNodeTokens {
	final List<Sequence> clones = new ArrayList<>();
	private SequenceObservable seqObservable;

	public CloneDetection(SequenceObservable seqObservable) {
		this.seqObservable = seqObservable;
	}

	public List<Sequence> findChains(Location lastLoc, Progress progress) {
		for(Sequence buildingChains = new Sequence(); lastLoc!=null; lastLoc = lastLoc.getPrev()) {
			Sequence newClones = collectClones(lastLoc);
			if(!buildingChains.getLocations().isEmpty() || newClones.size()>=Settings.get().getMinCloneClassSize())
				buildingChains = makeValid(lastLoc, buildingChains, newClones); //Because of the recent additions the current sequence may be invalidated
			if(buildingChains.size() <= 1 || (lastLoc.getPrev()!=null && lastLoc.getPrev().getFile()!=lastLoc.getFile()))
				buildingChains.getLocations().clear();
			progress.next();
		}
		return clones;
	}


	private Sequence makeValid(Location lastLoc, Sequence oldClones, Sequence newClones) {
		Map<Location /*oldClones*/, Location /*newClones*/> validChains = oldClones.getLocations().stream().distinct().filter(oldClone -> 
			newClones.getLocations().stream().anyMatch(newClone -> newClone.getFile() == oldClone.getFile() && oldClone.getPrev()!=null && oldClone.getFile() == oldClone.getPrev().getFile()
		    && newClone.getContents().getRange()!= null && newClone.getContents().getRange().equals(oldClone.getPrev().getContents().getRange()))).collect(Collectors.toMap(e -> e, Location::getPrev));
		
		collectFinishedClones(oldClones, newClones, validChains);
		
		if(newClones.size()<Settings.get().getMinCloneClassSize())
			return new Sequence();
		
		mergeLocationsOnBasisOfChains(newClones, validChains);
		
		if(lastLoc.getPrev()==null || lastLoc.getPrev().getFile()!=lastLoc.getFile()) {
			checkValidClones(newClones, newClones.getLocations());
		}
		
		return newClones;
	}

	private void collectFinishedClones(Sequence oldClones, Sequence newClones, Map<Location, Location> validChains) {
		if(validChains.size()!=oldClones.size() && !oldClones.getLocations().isEmpty()) {
			checkValidClones(oldClones, oldClones.getLocations().stream().filter(e -> !newClones.getLocations().contains(e.getPrev())).collect(Collectors.toList()));
		}
	}

	private void mergeLocationsOnBasisOfChains(Sequence newClones, Map<Location, Location> validChains) {
		for(Entry<Location, Location> validChain : validChains.entrySet()) {
			Location l = validChain.getValue().mergeWith(validChain.getKey());
			newClones.getLocations().set(newClones.getLocations().indexOf(validChain.getValue()), l);
			validChain.setValue(l);
		}
	}

	private void checkValidClones(Sequence oldClones, List<Location> endedClones) {
		ListMap<Integer /*Sequence size*/, Location /* Clones */> cloneList = new ListMap<>();
		endedClones.stream().forEach(e -> cloneList.addTo(e.getAmountOfNodes(), e));
		
		for(Entry<Integer, List<Location>> entry : cloneList.entrySet()) {
			int amountOfNodes = entry.getKey();
			List<Location> l = entry.getValue();
			addAllNonEndedLocations(oldClones, amountOfNodes, l);
			createClone(l);
		}
	}

	private void createClone(List<Location> l) {
		Sequence newSequence = new Sequence(l);
		if(l.size()>1 && checkThresholds(newSequence) && !isDuplicate(newSequence)) {
			for(int i = clones.size()-1; i>=0 && isPossiblyRedundant(newSequence, clones.get(i)); i--) {
				if(isSubset(clones.get(i), newSequence))
					clones.remove(i);
			}
			clones.add(newSequence.isValid());
			seqObservable.sendUpdate(ProblemType.DUPLICATION, newSequence, newSequence.getTotalNodeVolume());
		}
	}

	private void addAllNonEndedLocations(Sequence oldClones, int amountOfNodes, List<Location> l) {
		for(Location l2 : oldClones.getLocations()) {
			if(!l.contains(l2) && l2.getAmountOfNodes()>=amountOfNodes)
				l.add(new Location(l2, getRange(l2, amountOfNodes)));
		}
	}
	
	private Range getRange(Location l, int amountOfNodes) {
		return getRange(l.getFirstNode()).withEnd(getRange(l.getContents().getNodes().get(amountOfNodes-1)).end);
	}

	private Sequence collectClones(Location lastLoc) {
		Sequence c = new Sequence();
		while(lastLoc!=null) {
			c.add(lastLoc);
			lastLoc = lastLoc.getClone();
			if(lastLoc!=null) 
				lastLoc.isVisited = true;
		}
		return c;
	}
}

