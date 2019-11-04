package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternLocation;

public class NotSimilar extends Matching {
	
	int lines;
	
	public NotSimilar() {}

	public NotSimilar(Location location) {
		if(location instanceof PatternLocation) {
			lines = ((PatternLocation)location).getRange().end.line-((PatternLocation)location).getRange().begin.line+1;
		} else {
			lines = location.getNumberOfLines();
		}
	}

	@Override
	public boolean isMoreImportant(Matching similarity) {
		return false;
	}

}
