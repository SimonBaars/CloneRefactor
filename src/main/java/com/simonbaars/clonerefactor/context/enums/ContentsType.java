package com.simonbaars.clonerefactor.context.enums;

public enum ContentsType{
	FULLMETHOD("Full Method"), 
	PARTIALMETHOD("Partial Method"), 
	SEVERALMETHODS("Several Methods"), 
	FULLCONSTRUCTOR("Full Constructor"),
	PARTIALCONSTRUCTOR("Partial Constructor"),
	ONLYFIELDS("Only Fields"), 
	FULLCLASS("Full Class"), 
	FULLINTERFACE("Full Interface"),
	FULLENUM("Full Enum"),
	OTHER("Other");
	
	private final String name;
	
	private ContentsType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}