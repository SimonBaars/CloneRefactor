package com.simonbaars.clonerefactor.refactoring.enums;

public enum MethodType {
	VOID("Void"),
	RETURNSASSIGNEDVARIABLE("Assign"),
	RETURNSDECLAREDVARIABLE("Declare");
	
	private String name;

	private MethodType(String name) {
		this.name=name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
