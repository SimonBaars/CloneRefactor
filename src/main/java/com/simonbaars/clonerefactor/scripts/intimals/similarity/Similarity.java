package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternLocation;

public abstract class Similarity {

	public Similarity() {
		// TODO Auto-generated constructor stub
	}

	public static Similarity determineSimilarity(PatternLocation pattern, Location clone) {
		if(!pattern.getFile().equals(clone.getFile())) {
			return new NotSimilar();
		}
		return null;
	}
	
	protected abstract boolean isMoreImportant(Similarity similarity);
}
