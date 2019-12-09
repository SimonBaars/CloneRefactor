package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternLocation;

public class NotSimilar implements Matching {
	
	private final int linesClone;
	private final int linesPattern;

	public NotSimilar(PatternLocation pattern, Location clone) {
		this.linesPattern = ((PatternLocation)pattern).actualRange().end.line-((PatternLocation)pattern).actualRange().begin.line+1;
		this.linesClone = clone.getNumberOfLines();
	}

	@Override
	public boolean isMoreImportant(Matching similarity) {
		return false;
	}

	@Override
	public double getMatchPercentage() {
		return 0D;
	}

	@Override
	public int getWeight() {
		return linesClone + linesPattern;
	}
	
	@Override
	public MatchType getMatchType() {
		return MatchType.NOMATCH;
	}
}
