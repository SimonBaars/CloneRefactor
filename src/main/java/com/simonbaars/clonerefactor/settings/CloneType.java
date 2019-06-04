package com.simonbaars.clonerefactor.settings;

public enum CloneType {
	TYPE1,TYPE2,TYPE3;

	public boolean isNotTypeOne() {
		return this!=TYPE1;
	}
	
	public int getTypeAsNumber() {
		return ordinal()+1;
	}
	
	public String getNicelyFormatted() {
		return name().charAt(0) + name().substring(1).toLowerCase();
	}
}
