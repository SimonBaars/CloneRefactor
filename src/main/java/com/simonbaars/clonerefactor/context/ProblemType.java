package com.simonbaars.clonerefactor.context;

import com.simonbaars.clonerefactor.context.context.enums.Risk;

public enum ProblemType {
	DUPLICATION("Duplication", 5, 10, 20),
	UNITINTERFACESIZE("Unit Interface Size", 2, 4, 6),
	UNITCOMPLEXITY("Unit Complexity", 10, 20, 50),
	LINEVOLUME("Line Volume", 15, 30, 60),
	TOKENVOLUME("Token Volume", 15*10, 30*10, 60*10);
	
	private final String name;
	private final int low;
	private final int mid;
	private final int high;
	
	private ProblemType(String name, int low, int mid, int high) {
		this.name= name;
		this.low=low;
		this.mid=mid;
		this.high=high;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public Risk getRisk(int score) {
		if(score<=low) {
			return Risk.LOW;
		} else if(score<=mid) {
			return Risk.MODERATE;
		} else if(score<=high) {
			return Risk.HIGH;
		}
		return Risk.VERYHIGH;
	}
}
