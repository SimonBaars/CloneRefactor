package com.simonbaars.clonerefactor.metrics.context.enums;
public enum RelationType { //Please note that the order of these enum constants matters
		SAMEMETHOD, // Refactor to same class as a private method
		SAMECLASS, // Refactor to same class as a private method
		SUPERCLASS, // Refactor to topmost class as a protected method
		ANCESTOR, // Refactor to common parent class as a protected method
		SIBLING, // Refactor to common parent class as a protected method
		FIRSTCOUSIN, // Refactor to common parent class as a protected method
		COMMONHIERARCHY, // Refactor to common parent class as a protected method
		SAMEINTERFACE, // Refactor common interface as an default method
		NODIRECTSUPERCLASS, // Refactor to newly created abstract class as a protected method
		EXTERNALSUPERCLASS, // Refactor to newly created interface as a default method
		UNRELATED // Refactor to newly created interface as a default method
	}