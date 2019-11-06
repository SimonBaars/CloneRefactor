package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternLocation;

public class Intersects extends Matching implements CalculatesPercentages {
	
	private final PatternLocation pattern;
	private final Location clone;
	
	private int unmatchedClone = 0;
	private int unmatchedPattern = 0;
	private int matched = 0;
	
	public Intersects() {
		pattern = null;
		clone = null;
	}
	
	public Intersects(PatternLocation pattern, Location clone) {
		this.pattern = pattern;
		this.clone = clone;
		Range actualPatternRange = pattern.actualRange();
		Position begin = actualPatternRange.begin.isBefore(clone.getRange().begin) ? actualPatternRange.begin : clone.getRange().begin;
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
		}
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

}
