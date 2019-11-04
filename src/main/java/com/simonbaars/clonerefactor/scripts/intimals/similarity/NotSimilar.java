package com.simonbaars.clonerefactor.scripts.intimals.similarity;

public class NotSimilar extends Matching {

	public NotSimilar() {}

	@Override
	public boolean isMoreImportant(Matching similarity) {
		return false;
	}

}
