package com.simonbaars.clonerefactor.metrics;

public enum ProblemType {
	DUPLICATION("Duplication"),
	UNITINTERFACESIZE("Unit Interface Size"),
	UNITCOMPLEXITY("Unit Complexity"),
	LINEVOLUME("Line Volume"),
	TOKENVOLUME("Token Volume");
	
	private final String name;
	
	private ProblemType(String name) {
		this.name= name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
