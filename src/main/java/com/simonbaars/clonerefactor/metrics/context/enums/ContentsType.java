package com.simonbaars.clonerefactor.metrics.context.enums;

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
	HASCLASSDECLARATION("Has Class Declaration"), 
	HASINTERFACEDECLARATION("Has Interface Declaration"), 
	HASENUMDECLARATION("Has Enum Declaration"), 
	HASENUMFIELDS("Has Enum Fields"),
	INCLUDESFIELDS("Includes Fields"),
	INCLUDESCONSTRUCTOR("Includes Constructor"),
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