package com.simonbaars.clonerefactor.metrics.context.enums;

public enum Risk {
	LOW("Low"), 
	MODERATE("Moderate"), 
	HIGH("High"), 
	VERYHIGH("Very high");
	
	private final String name;
	
	private Risk(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public String lowercase() {
		return toString().toLowerCase();
	}
}
