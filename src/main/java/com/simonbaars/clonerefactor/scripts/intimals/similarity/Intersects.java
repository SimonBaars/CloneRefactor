package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternLocation;

public class Intersects extends Similarity {
	
	private int unmatchedClone;
	private int unmatchedPattern;
	private int matched;
	
	public Intersects(int unmatchedClone, int unmatchedPattern, int matched) {
		super();
		this.unmatchedClone = unmatchedClone;
		this.unmatchedPattern = unmatchedPattern;
		this.matched = matched;
	}

	public Intersects(PatternLocation pattern, Location clone) {
		//TODO
	}

	@Override
	protected boolean isMoreImportant(Similarity similarity) {
		if(!(similarity instanceof Intersects))
			return true;
		Intersects other = (Intersects)similarity;
		return getMatchedMinusUnmatched() > other.getMatchedMinusUnmatched();
	}
	
	private int getMatchedMinusUnmatched() {
		return matched - unmatchedClone - unmatchedPattern;
	}

}
