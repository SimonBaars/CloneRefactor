package com.simonbaars.clonerefactor.context.context.enums;

public enum LocationType{
	METHODLEVEL("Method Level"),
	CONSTRUCTORLEVEL("Constructor Level"),
	CLASSLEVEL("Class Level"),
	INTERFACELEVEL("Interface Level"),
	ENUMLEVEL("Enum Level"),
	OUTSIDE("Outside");
	
	private final String name;
	
	private LocationType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}