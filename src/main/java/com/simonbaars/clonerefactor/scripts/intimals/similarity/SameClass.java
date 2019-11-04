package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternLocation;

public class SameClass extends Similarity {
	
	private int distance;

	public SameClass(int distance) {
		this.distance = distance;
	}

	public SameClass(PatternLocation pattern, Location clone) {
		if(pattern.actualRange().end.isBefore(clone.getRange().begin)) {
			this.distance = clone.getRange().begin.line-pattern.actualRange().end.line;
		} else {
			this.distance = pattern.actualRange().begin.line-clone.getRange().end.line;
		}
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
