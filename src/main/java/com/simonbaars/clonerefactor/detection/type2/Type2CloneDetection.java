package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.simonbaars.clonerefactor.datatype.ListMap;

public class Type2CloneDetection  {
	final List<Type2Sequence> clones = new ArrayList<>();

	public Type2CloneDetection() {}

	public List<Type2Sequence> findChains(Type2Location lastLoc) {
		for(Type2Sequence buildingChains = new Type2Sequence(); lastLoc!=null; lastLoc = lastLoc.getPrev()) {
			Type2Sequence newClones = collectClones(lastLoc);
			if(!buildingChains.getSequence().isEmpty() || newClones.size()>1)
				buildingChains = makeValid(lastLoc, buildingChains, newClones); //Because of the recent additions the current sequence may be invalidated
			if(buildingChains.size() == 1 || (lastLoc.getPrev()!=null && lastLoc.getPrev().getLocationIndex()!=lastLoc.getLocationIndex()))
				buildingChains.getSequence().clear();
		}
		clones.forEach(Type2Sequence::tryToExpand);
		return clones;
	}

	private Type2Sequence makeValid(Type2Location lastLoc, Type2Sequence buildingChains, Type2Sequence newClones) {
		Map<Type2Location /*oldClones*/, Type2Location /*newClones*/> validChains = buildingChains.getSequence().stream().distinct().filter(oldClone -> 
			newClones.getSequence().stream().anyMatch(newClone -> newClone.getLocationIndex() == oldClone.getLocationIndex() && oldClone.getPrev()!=null && oldClone.getLocationIndex() == oldClone.getPrev().getLocationIndex()
			&& newClone.getStatementIndex() == oldClone.getPrev().getLocationIndex())).collect(Collectors.toMap(e -> e, Type2Location::getPrev));
		
		collectFinishedClones(buildingChains, newClones, validChains);
		mergeLocationsOnBasisOfChains(newClones, validChains);
		
		if(lastLoc.getPrev()==null || lastLoc.getPrev().getLocationIndex()!=lastLoc.getLocationIndex()) {
			checkValidClones(newClones, newClones.getSequence());
		}
		
		return newClones;
	}

	private void collectFinishedClones(Type2Sequence buildingChains, Type2Sequence newClones, Map<Type2Location, Type2Location> validChains) {
		if(validChains.size()!=buildingChains.size() && !buildingChains.getSequence().isEmpty()) {
			checkValidClones(buildingChains, buildingChains.getSequence().stream().filter(e -> !newClones.getSequence().contains(e.getPrev())).collect(Collectors.toList()));
		}
	}

	private void mergeLocationsOnBasisOfChains(Type2Sequence newClones, Map<Type2Location, Type2Location> validChains) {
		for(Entry<Type2Location, Type2Location> validChain : validChains.entrySet()) {
			Type2Location l = validChain.getValue().mergeWith(validChain.getKey());
			newClones.getSequence().set(newClones.getSequence().indexOf(validChain.getValue()), l);
			validChain.setValue(l);
		}
	}

	private void checkValidClones(Type2Sequence buildingChains, List<Type2Location> list) {
		ListMap<Integer /*Sequence size*/, Type2Location /* Clones */> cloneList = new ListMap<>();
		list.stream().forEach(e -> cloneList.addTo(e.getAmountOfNodes(), e));
		for(Entry<Integer, List<Type2Location>> entry : cloneList.entrySet()) {
			int amountOfNodes = entry.getKey();
			List<Type2Location> l = entry.getValue();
			addAllNonEndedLocations(buildingChains, amountOfNodes, l);
			createClone(l);
		}
	}

	private void createClone(List<Type2Location> l) {
		Type2Sequence newSequence = new Type2Sequence(l);
		if(l.size()>1) {
			clones.add(newSequence);
		}
	}

	private void addAllNonEndedLocations(Type2Sequence buildingChains, int amountOfNodes, List<Type2Location> l) {
		for(Type2Location l2 : buildingChains.getSequence()) {
			if(!l.contains(l2) && l2.getAmountOfNodes()>=amountOfNodes) {
				l.add(l2);
			}
		}
	}

	private Type2Sequence collectClones(Type2Location lastLoc) {
		return new Type2Sequence(lastLoc.getContents().getStatementsWithinThreshold());
	}
}

