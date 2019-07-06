package com.simonbaars.clonerefactor.refactoring.target;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.SimpleName;

public class ExtractToNewInterface extends ExtractToClassOrInterface {
	private static int x = 0;
	
	public ExtractToNewInterface() {
		super(new ClassOrInterfaceDeclaration(new NodeList<>(), new NodeList<>(), true, new SimpleName("GeneratedInterface"+(x++)), new NodeList<>(), new NodeList<>(), new NodeList<>(), new NodeList<>()));
	}

}
