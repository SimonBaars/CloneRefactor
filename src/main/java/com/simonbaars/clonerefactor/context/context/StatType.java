package com.simonbaars.clonerefactor.context.context;

public enum StatType {
	TOTAL, CLONED, OVERLAPPING;
	
	@Override
	public String toString() {
		return name().charAt(0)+name().substring(1).toLowerCase();
	}
}
