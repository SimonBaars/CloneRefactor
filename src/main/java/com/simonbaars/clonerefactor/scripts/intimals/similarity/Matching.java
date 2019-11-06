package com.simonbaars.clonerefactor.scripts.intimals.similarity;

public abstract class Matching implements HasImportance<Matching> {

	public Matching() {}

	public abstract double getMatchPercentage();
	public abstract int getWeight();
}
