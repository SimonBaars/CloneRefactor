package com.simonbaars.clonerefactor.refactoring;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class ExtractToClassOrInterface {
	private final ClassOrInterfaceDeclaration classOrInterface;

	public ExtractToClassOrInterface(ClassOrInterfaceDeclaration classOrInterface) {
		super();
		this.classOrInterface = classOrInterface;
	}

	public ClassOrInterfaceDeclaration getClassOrInterface() {
		return classOrInterface;
	}
}
