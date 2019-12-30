package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import java.util.Set;

import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternLocation;
import com.simonbaars.clonerefactor.scripts.intimals.model.SimpleRange;

public class Intersects implements Matching, CalculatesPercentages {
	
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
		Set<Integer> patternLines = pattern.lines(), cloneLines = clone.lines();
		cloneLines.retainAll(patternLines);
		this.matched = cloneLines.size();
		cloneLines = clone.lines();
		cloneLines.removeAll(patternLines);
		patternLines.removeAll(clone.lines());
		this.unmatchedClone = cloneLines.size();
		this.unmatchedPattern = patternLines.size();
		this.matchType = MatchType.determine(new SimpleRange(pattern.actualRange()), new SimpleRange(clone.getRange()));
	}
	
	@Override
	public boolean isMoreImportant(Matching other) {
		if(other instanceof NotSimilar)
			return true;
		return getMatchPercentage() > ((Intersects)other).getMatchPercentage();
	}
	
	@Override
	public double getMatchPercentage() {
		return calcPercentage(matched*2, getWeight()); 
	}

	@Override
	public String toString() {
		return "Intersects [unmatchedClone=" + unmatchedClone + ", unmatchedPattern=" + unmatchedPattern + ", matched="
				+ matched + "]";
	}

	@Override
	public int getWeight() {
		return (matched*2)+unmatchedClone+unmatchedPattern;
	}

	public PatternLocation getPattern() {
		return pattern;
	}

	public Location getClone() {
		return clone;
	}

	@Override
	public MatchType getMatchType() {
		return matchType;
	}

}
