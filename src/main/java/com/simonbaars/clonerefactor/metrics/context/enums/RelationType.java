package com.simonbaars.clonerefactor.metrics.context.enums;

public enum RelationType { //Please note that the order of these enum constants matters
	SAMEMETHOD("Same Method"), // Refactor to same class as a private method
	SAMECLASS("Same Class"), // Refactor to same class as a private method
	SUPERCLASS("Superclass"), // Refactor to topmost class as a protected method
	SIBLING("Sibling"), // Refactor to common parent class as a protected method
	ANCESTOR("Ancestor"), // Refactor to common parent class as a protected method
	FIRSTCOUSIN("First Cousin"), // Refactor to common parent class as a protected method
	COMMONHIERARCHY("Common Hierarchy"), // Refactor to common parent class as a protected method
	SAMEINTERFACE("Same Interface"), // Refactor common interface as an default method
	NODIRECTSUPERCLASS("No Direct Superclass"), // Refactor to newly created abstract class as a protected method
	NOINDIRECTSUPERCLASS("No Indirect Superclass"), // Refactor to newly created abstract class as a protected method
	EXTERNALSUPERCLASS("External Superclass"), // Refactor to newly created interface as a default method
	EXTERNALANCESTOR("External Ancestor"), // Refactor to newly created interface as a default method
	UNRELATED("Unrelated") // Refactor to newly created interface as a default method
	;
	
	private final String name;
	
	private RelationType(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}