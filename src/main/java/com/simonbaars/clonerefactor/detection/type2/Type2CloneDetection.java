package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.simonbaars.clonerefactor.datatype.map.ListMap;
import com.simonbaars.clonerefactor.detection.interfaces.HasSettings;
import com.simonbaars.clonerefactor.detection.type2.model.Type2Location;
import com.simonbaars.clonerefactor.detection.type2.model.Type2Sequence;
import com.simonbaars.clonerefactor.settings.Settings;

public class Type2CloneDetection extends HasSettings {
	final List<Type2Sequence> clones = new ArrayList<>();

	public Type2CloneDetection(Settings settings) {
		super(settings);
	}

	public List<Type2Sequence> findChains(Type2Location lastLoc) {
		for(Type2Sequence buildingChains = new Type2Sequence(); lastLoc!=null; lastLoc = lastLoc.getPrev()) {
			Type2Sequence newClones = collectClones(lastLoc);
			if(!buildingChains.getSequence().isEmpty() || newClones.size()>1)
				buildingChains = makeValid(lastLoc, buildingChains, newClones); //Because of the recent additions the current sequence may be invalidated
			if(buildingChains.size() == 1 || (lastLoc.getPrev()!=null && lastLoc.getPrev().getLocationIndex()!=lastLoc.getLocationIndex()))
				buildingChains.getSequence().clear();
		}
		return tryToExpand(clones);
	}
	
	public List<Type2Sequence> tryToExpand(List<Type2Sequence> clones){
		for(int i = 0; i<clones.size(); i++)
			clones.get(i).tryToExpand(this::checkType2VariabilityThreshold, clones);
		return clones;
	}

	private Type2Sequence makeValid(Type2Location lastLoc, Type2Sequence buildingChains, Type2Sequence newClones) {
		Map<Type2Location /*oldClones*/, Type2Location /*newClones*/> validChains = buildingChains.getSequence().stream().distinct().filter(oldClone -> 
			newClones.getSequence().stream().anyMatch(newClone -> newClone.getLocationIndex() == oldClone.getLocationIndex() && oldClone.getPrev()!=null && oldClone.getLocationIndex() == oldClone.getPrev().getLocationIndex()
			&& newClone.getStatementIndex() == oldClone.getPrev().getStatementIndex())).collect(Collectors.toMap(e -> e, Type2Location::getPrev));
		
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
		list.stream().forEach(e -> cloneList.addTo(e.size(), e));
		for(Entry<Integer, List<Type2Location>> entry : cloneList.entrySet()) {
			int amountOfNodes = entry.getKey();
			List<Type2Location> l = entry.getValue();
			addAllNonEndedLocations(buildingChains, amountOfNodes, l);
			createClone(l);
		}
	}

	private void createClone(List<Type2Location> l) {
		Type2Sequence newSequence = new Type2Sequence(l);
		if(checkThresholds(newSequence))
			clones.add(newSequence);
	}

	private void addAllNonEndedLocations(Type2Sequence buildingChains, int amountOfNodes, List<Type2Location> l) {
		for(Type2Location l2 : buildingChains.getSequence()) {
			if(!l.contains(l2) && l2.size()>=amountOfNodes) {
				l.add(setSize(l2, amountOfNodes));
			}
		}
	}

	private Type2Location setSize(Type2Location l2, int amountOfNodes) {
		return l2.withSize(amountOfNodes);
	}

	private Type2Sequence collectClones(Type2Location lastLoc) {
		return new Type2Sequence(lastLoc.getFirstContents().getStatementsWithinThreshold(this::checkType2VariabilityThreshold));
	}
}

