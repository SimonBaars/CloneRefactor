package com.simonbaars.clonerefactor.scripts.intimals.similarity;

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
