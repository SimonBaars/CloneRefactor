package com.simonbaars.clonerefactor.scripts.intimals.similarity;

public interface Matching extends HasImportance<Matching> {
	public double getMatchPercentage();
	public int getWeight();
	public MatchType getMatchType();
}
