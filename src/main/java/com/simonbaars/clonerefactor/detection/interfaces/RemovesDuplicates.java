package com.simonbaars.clonerefactor.detection.interfaces;

import java.util.List;

import com.simonbaars.clonerefactor.model.Sequence;

public interface RemovesDuplicates {
	//TODO: We should optimise this method. It takes up 5%+ of the total runtime.
	public default boolean removeDuplicatesOf(List<Sequence> clones, Sequence l) {
		//return true;
		clones.removeIf(e -> isSubset(e, l));
		return clones.stream().noneMatch(e -> isSubset(l, e));
	}
	
	public default boolean isDuplicate(Sequence l) {
		return l.getLocations().stream().allMatch(e -> e.isVisited);
	}
	
	public default boolean isPossiblyRedundant(Sequence newSeq, Sequence prev) {
		return newSeq.getLocations().stream().anyMatch(e -> prev.getLocations().stream().anyMatch(f -> e.getRange().contains(f.getRange())));
	}
	
	public default boolean isSubset(Sequence existentClone, Sequence newClone) {
		return existentClone.getLocations().stream().allMatch(oldLoc -> newClone.getLocations().stream().anyMatch(newLoc -> oldLoc.getFile() == newLoc.getFile() && newLoc.getRange().contains(oldLoc.getRange())));
	}
}
