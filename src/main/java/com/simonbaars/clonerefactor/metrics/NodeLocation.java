package com.simonbaars.clonerefactor.metrics;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public enum NodeLocation {
	COMMONHIERARCHY,
	UNRELATED,
	SUPERCLASS,
	ANCESTOR,
	SAMECLASS,
	SAMEMETHOD,
	SAMEINTERFACE;
	
	private NodeLocation() {}
	
	public static NodeLocation getLocation(Node n1, Node n2) {
		MethodDeclaration m1 = getMethod(n1);
		MethodDeclaration m2 = getMethod(n2);
		if(m1!=null && m1.equals(m2))
			return SAMEMETHOD;
		ClassOrInterfaceDeclaration c1 = getClass(n1);
		ClassOrInterfaceDeclaration c2 = getClass(n2);
		if(c1!=null && c1.equals(c2))
			return SAMECLASS;
		if(!c1.getExtendedTypes().isEmpty() && )
		return UNRELATED;
	}

	private static MethodDeclaration getMethod(Node n1) {
		while (!(n1 instanceof MethodDeclaration)) {
			if(n1.getParentNode().isPresent()) {
				n1 = n1.getParentNode().get();
			} else return null;
		}
		return (MethodDeclaration)n1;
	}
	
	private static ClassOrInterfaceDeclaration getClass(Node n1) {
		while (!(n1 instanceof ClassOrInterfaceDeclaration)) {
			if(n1.getParentNode().isPresent()) {
				n1 = n1.getParentNode().get();
			} else return null;
		}
		return (ClassOrInterfaceDeclaration)n1;
	}
}
