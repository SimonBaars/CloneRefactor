package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternLocation;
import com.simonbaars.clonerefactor.scripts.intimals.model.SimpleRange;

public class Intersects extends Matching implements CalculatesPercentages {
	
	private final PatternLocation pattern;
	private final Location clone;
	
	private int unmatchedClone = 0;
	private int unmatchedPattern = 0;
	private int matched = 0;
	
	private final MatchType matchType;
	
	public Intersects() {
		pattern = null;
		clone = null;
		matchType = null;
	}
	
	public Intersects(PatternLocation pattern, Location clone) {
		this.pattern = pattern;
		this.clone = clone;
		SimpleRange patternRange = new SimpleRange(pattern.actualRange());
		SimpleRange cloneRange = new SimpleRange(clone.getRange());
		/*Position begin = actualPatternRange.begin.isBefore(clone.getRange().begin) ? actualPatternRange.begin : clone.getRange().begin;
		Position end = actualPatternRange.end.isAfter(clone.getRange().end) ? actualPatternRange.end : clone.getRange().end;
		for(int i = begin.line; i<=end.line; i++) {
			boolean patternIntersects = i >= actualPatternRange.begin.line && i <= actualPatternRange.end.line;
			boolean cloneIntersects = i >= clone.getRange().begin.line && i <= clone.getRange().end.line;
			if(patternIntersects && cloneIntersects) {
				matched++;
			} else if(patternIntersects) {
				unmatchedPattern++;
			} else if(cloneIntersects) {
				unmatchedClone++;
			}
		}*/
		this.matchType = MatchType.determine(patternRange, cloneRange);
		assert matched != 0;
	}
	
	public boolean isMoreImportant(Matching other) {
		if(other instanceof NotSimilar)
			return true;
		return getMatchPercentage() > ((Intersects)other).getMatchPercentage();
	}
	
	public double getMatchPercentage() {
		return calcPercentage(matched*2, getWeight()); 
	}

	@Override
	public String toString() {
		return "Intersects [unmatchedClone=" + unmatchedClone + ", unmatchedPattern=" + unmatchedPattern + ", matched="
				+ matched + "]";
	}

	public int getWeight() {
		return (matched*2)+unmatchedClone+unmatchedPattern;
	}

	public PatternLocation getPattern() {
		return pattern;
	}

	public Location getClone() {
		return clone;
	}

	public MatchType getMatchType() {
		return matchType;
	}

}
