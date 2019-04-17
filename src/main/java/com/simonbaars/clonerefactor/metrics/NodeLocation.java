package com.simonbaars.clonerefactor.metrics;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public enum NodeLocation {
	COMMONHIERARCHY,
	UNRELATED, //done
	SUPERCLASS, //done
	ANCESTOR,
	SIBLING,
	SAMECLASS, //done
	SAMEMETHOD, //done
	SAMEINTERFACE;
	
	private NodeLocation() {}
	
	public static NodeLocation getLocation(Node n1, Node n2) {
		if(isMethod(n1, n2))
			return SAMEMETHOD;
		ClassOrInterfaceDeclaration c1 = getClass(n1);
		ClassOrInterfaceDeclaration c2 = getClass(n2);
		if(c1!=null && c1.equals(c2))
			return SAMECLASS;
		if(isSuperClass(c1, c2) || isSuperClass(c2, c1)) 
			return SUPERCLASS;
		
		
		return UNRELATED;
	}

	private static boolean isMethod(Node n1, Node n2) {
		MethodDeclaration m1 = getMethod(n1);
		MethodDeclaration m2 = getMethod(n2);
		return m1!=null && m1.equals(m2);
	}

	private static boolean isSuperClass(ClassOrInterfaceDeclaration c1, ClassOrInterfaceDeclaration c2) {
		return c1.getExtendedTypes().stream().anyMatch(e -> getFullyQualifiedName(c2).equals(getFullyQualifiedName(e)));
	}

	private static String getFullyQualifiedName(ClassOrInterfaceType e) {
		String name = "";
		if(e.getScope().isPresent())
			name+=getFullyQualifiedName(e.getScope().get())+".";
		return name+e.getNameAsString();
	}

	private static Object getFullyQualifiedName(ClassOrInterfaceDeclaration c2) {
		String name = "";
		ClassOrInterfaceDeclaration parentClass = getClass(c2);
		if(parentClass!=null) {
			name+=getFullyQualifiedName(parentClass)+".";
		} else {
			CompilationUnit u = getCompilationUnit(c2);
			if(u!=null && u.getPackageDeclaration().isPresent()) {
				name+=u.getPackageDeclaration().get().getNameAsString()+".";
			}
		}
		return name+c2.getNameAsString();
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
	
	private static CompilationUnit getCompilationUnit(Node n1) {
		while (!(n1 instanceof CompilationUnit)) {
			if(n1.getParentNode().isPresent()) {
				n1 = n1.getParentNode().get();
			} else return null;
		}
		return (CompilationUnit)n1;
	}
}
