package com.simonbaars.clonerefactor.settings;

public enum CloneType {
	TYPE1R,TYPE2R,TYPE3R,
	TYPE1, TYPE2, TYPE3;

	public boolean isNotType1() {
		return !isType1();
	}
	
	public boolean isType1() {
		return this==TYPE1R || this==TYPE1;
	}
	
	public boolean isType2() {
		return this==TYPE2R || this==TYPE2;
	}
	
	public boolean isType3() {
		return this==TYPE3R || this==TYPE3;
	}
	
	public int getTypeAsNumber() {
		return ordinal()+1;
	}
	
	public String getNicelyFormatted() {
		return name().charAt(0) + name().substring(1,4).toLowerCase() + " " + name().substring(5);
	}
	
	public boolean isRefactoringOriented() {
		return name().endsWith("R");
	}
	
	@Override
	public String toString() {
		return getNicelyFormatted();
	}
}
