package com.simonbaars.clonerefactor.metrics.model;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class ComparingClasses {
	private final ClassOrInterfaceDeclaration classOne;
	private final ClassOrInterfaceDeclaration classTwo;
	
	public ComparingClasses(ClassOrInterfaceDeclaration classOne, ClassOrInterfaceDeclaration classTwo) {
		super();
		this.classOne = classOne;
		this.classTwo = classTwo;
	}
	
	public ClassOrInterfaceDeclaration getClassOne() {
		return classOne;
	}
	public ClassOrInterfaceDeclaration getClassTwo() {
		return classTwo;
	}

	public ComparingClasses reverse() {
		return new ComparingClasses(classTwo, classOne);
	}

	public boolean invalid() {
		return classOne == null || classTwo == null;
	}
	
	
}
