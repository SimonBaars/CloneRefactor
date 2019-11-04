package com.simonbaars.clonerefactor.scripts.intimals.similarity;

public class NotSimilar implements HasImportance {

	public NotSimilar() {}

	@Override
	public boolean isMoreImportant(HasImportance similarity) {
		return false;
	}

}
