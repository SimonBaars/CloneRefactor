package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.SequenceObservable;
import com.simonbaars.clonerefactor.ast.interfaces.DeterminesNodeTokens;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.detection.interfaces.ChecksThresholds;
import com.simonbaars.clonerefactor.detection.interfaces.RemovesDuplicates;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class Type2CloneDetection implements ChecksThresholds, RemovesDuplicates, DeterminesNodeTokens {
	final List<Sequence> clones = new ArrayList<>();

	public Type2CloneDetection() {}

	public List<Sequence> findChains(Type2Statement lastLoc) {
		for(Type2Sequence buildingChains = new Type2Sequence(); lastLoc!=null; lastLoc = lastLoc.getPrev()) {
			Type2Sequence newClones = collectClones(lastLoc);
			if(!buildingChains.getSequence().isEmpty() || newClones.size()>1)
				buildingChains = makeValid(lastLoc, buildingChains, newClones); //Because of the recent additions the current sequence may be invalidated
			if(buildingChains.size() == 1 || (lastLoc.getPrev()!=null && lastLoc.getPrev().getLocationIndex()!=lastLoc.getLocationIndex()))
				buildingChains.getSequence().clear();
		}
		return clones;
	}


	private Type2Sequence makeValid(Type2Statement lastLoc, Type2Sequence buildingChains, Type2Sequence newClones) {
		Map<Type2Statement /*oldClones*/, Type2Statement /*newClones*/> validChains = buildingChains.getSequence().stream().distinct().filter(oldClone -> 
			newClones.getSequence().stream().anyMatch(newClone -> newClone.getLocationIndex() == oldClone.getLocationIndex() && oldClone.getPrev()!=null && oldClone.getLocationIndex() == oldClone.getPrev().getLocationIndex()
			&& newClone.getStatementIndex() == oldClone.getPrev().getLocationIndex())).collect(Collectors.toMap(e -> e, Type2Statement::getPrev));
		
		collectFinishedClones(buildingChains, newClones, validChains);
		mergeLocationsOnBasisOfChains(newClones, validChains);
		
		if(lastLoc.getPrev()==null || lastLoc.getPrev().getLocationIndex()!=lastLoc.getLocationIndex()) {
			checkValidClones(newClones, newClones.getSequence());
		}
		
		return newClones;
	}

	private void collectFinishedClones(Type2Sequence buildingChains, Type2Sequence newClones, Map<Type2Statement, Type2Statement> validChains) {
		if(validChains.size()!=buildingChains.size() && !buildingChains.getSequence().isEmpty()) {
			checkValidClones(buildingChains, buildingChains.getSequence().stream().filter(e -> !newClones.getSequence().contains(e.getPrev())).collect(Collectors.toList()));
		}
	}

	private void mergeLocationsOnBasisOfChains(Type2Sequence newClones, Map<Type2Statement, Type2Statement> validChains) {
		for(Entry<Type2Statement, Type2Statement> validChain : validChains.entrySet()) {
			Type2Statement l = validChain.getValue().mergeWith(validChain.getKey());
			newClones.getSequence().set(newClones.getSequence().indexOf(validChain.getValue()), l);
			validChain.setValue(l);
		}
	}

	private void checkValidClones(Type2Sequence buildingChains, List<Type2Statement> list) {
		ListMap<Integer /*Sequence size*/, Location /* Clones */> cloneList = new ListMap<>();
		list.stream().forEach(e -> cloneList.addTo(e.getAmountOfNodes(), e));
		for(Entry<Integer, List<Location>> entry : cloneList.entrySet()) {
			int amountOfNodes = entry.getKey();
			List<Location> l = entry.getValue();
			addAllNonEndedLocations(buildingChains, amountOfNodes, l);
			createClone(l);
		}
	}

	private void createClone(List<Location> l) {
		Sequence newSequence = new Sequence(l);
		if(l.size()>1 && checkThresholds(newSequence)) {
			newSequence.isValid();
			if(removeDuplicatesOf(clones, newSequence)) {
				clones.add(newSequence);
				SequenceObservable.get().sendUpdate(ProblemType.DUPLICATION, newSequence, newSequence.getTotalNodeVolume());
			}
		}
	}

	private void addAllNonEndedLocations(Sequence oldClones, int amountOfNodes, List<Location> l) {
		for(Location l2 : oldClones.getSequence()) {
			if(!l.contains(l2) && l2.getAmountOfNodes()>=amountOfNodes) {
				l.add(new Location(l2, getRange(l2, amountOfNodes)));
			}
		}
	}
	
	private Range getRange(Location l, int amountOfNodes) {
		return getRange(l.getContents().getNodes().get(0)).withEnd(getRange(l.getContents().getNodes().get(amountOfNodes-1)).end);
	}

	private Type2Sequence collectClones(Type2Statement lastLoc) {
		return new Type2Sequence(lastLoc.getContents().getStatementsWithinThreshold());
	}
}

