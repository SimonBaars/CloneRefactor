package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import com.simonbaars.clonerefactor.scripts.intimals.model.SimpleRange;

public enum MatchType {
	CLONEINPATTERN, PATTERNINCLONE, OVERFLOW, EXACTMATCH, NOMATCH;

	public static MatchType determine(SimpleRange patternRange, SimpleRange cloneRange) {
		if(patternRange.equals(cloneRange))
			return EXACTMATCH;
		else if(patternRange.contains(cloneRange))
			return CLONEINPATTERN;
		else if(cloneRange.contains(patternRange))
			return CLONEINPATTERN;
		return OVERFLOW;
	}
}
