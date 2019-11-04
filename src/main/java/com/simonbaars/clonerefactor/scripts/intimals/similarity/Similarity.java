package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternLocation;

public abstract class Similarity {

	public Similarity() {}
	
	public static Similarity determineSimilarity(PatternLocation pattern, List<Location> clones) {
		List<Similarity> similarities = new ArrayList<>();
		for(Location clone : clones) {
			similarities.add(determineSimilarity(pattern, clone));
		}
		return similarities.stream().reduce((e1, e2) -> e1.isMoreImportant(e2) ? e1 : e2).get();
	}
	
	public static Similarity determineSimilarity(List<PatternLocation> patterns, Location clone) {
		List<Similarity> similarities = new ArrayList<>();
		for(PatternLocation pattern : patterns) {
			similarities.add(determineSimilarity(pattern, clone));
		}
		return similarities.stream().reduce((e1, e2) -> e1.isMoreImportant(e2) ? e1 : e2).get();
	}

	private static Similarity determineSimilarity(PatternLocation pattern, Location clone) {
		if(!pattern.getFile().equals(clone.getFile())) {
			return new NotSimilar();
		} else if (pattern.actualRange().overlapsWith(clone.getRange())) {
			return new Intersects(pattern, clone);
		}
		return new SameClass(pattern, clone);
	}
	
	protected abstract boolean isMoreImportant(Similarity similarity);
}
