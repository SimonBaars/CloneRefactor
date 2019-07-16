package com.simonbaars.clonerefactor.detection.interfaces;

import java.util.List;

import com.simonbaars.clonerefactor.model.Sequence;

public interface RemovesDuplicates {
	public default boolean removeDuplicatesOf(List<Sequence> clones, Sequence newClone) {
		clones.removeIf(existingClone -> isSubset(existingClone, newClone));
		return clones.stream().noneMatch(e -> isSubset(newClone, e));
	}
	
	public default boolean isDuplicate(Sequence l) {
		return l.getLocations().stream().allMatch(e -> e.isVisited);
	}
	
	public default boolean isPossiblyRedundant(Sequence newSeq, Sequence prev) {
		return newSeq.getLocations().stream().anyMatch(e -> prev.getLocations().stream().anyMatch(f -> e.getRange().contains(f.getRange())));
	}
	
	public default boolean isSubset(Sequence existingClone, Sequence newClone) {
		return existingClone.getLocations().stream().allMatch(oldLoc -> newClone.getLocations().stream().anyMatch(newLoc -> oldLoc.getFile() == newLoc.getFile() && newLoc.getRange().contains(oldLoc.getRange())));
	}
}
