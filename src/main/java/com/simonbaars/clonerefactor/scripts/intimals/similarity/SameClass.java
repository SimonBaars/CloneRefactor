package com.simonbaars.clonerefactor.scripts.intimals.similarity;

public class SameClass extends Similarity {
	
	private int distance;

	public SameClass(int distance) {
		this.distance = distance;
	}

	@Override
	protected boolean isMoreImportant(Similarity similarity) {
		if(similarity instanceof NotSimilar) 
			return true;
		else if (similarity instanceof Intersects)
			return false;
		return distance<((SameClass)similarity).distance;
	}

}
