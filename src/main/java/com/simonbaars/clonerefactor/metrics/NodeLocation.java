package com.simonbaars.clonerefactor.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.simonbaars.clonerefactor.model.Sequence;

public enum NodeLocation { //Please note that the order of these enum values matters
	SAMEMETHOD, //done
	SAMECLASS, //done
	SUPERCLASS, //done
	ANCESTOR, //done
	SIBLING, //done
	FIRSTCOUSIN, //done
	SAMEINTERFACE,
	COMMONHIERARCHY,
	UNRELATED //done
	;
	
	private static final Map<String, ClassOrInterfaceDeclaration> classes = new HashMap<>();
	
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
		if(isAncestor(c1,c2) || isAncestor(c2,c1))
			return ANCESTOR;
		if(isSiblingOrCousin(c1, c2, 1))
			return SIBLING;
		if(isSiblingOrCousin(c1, c2, 2))
			return FIRSTCOUSIN;
		return UNRELATED;
	}
	
	public static void registerNode(Node n) {
		if(n instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration n2 = (ClassOrInterfaceDeclaration)n;
			classes.put(getFullyQualifiedName(n2), n2);
		}
	}
	
	public static void clearClasses() {
		classes.clear();
	}

	private static boolean isSiblingOrCousin(ClassOrInterfaceDeclaration c1, ClassOrInterfaceDeclaration c2, int i) {
		ClassOrInterfaceDeclaration parent1 = goUp(c1, i);
		ClassOrInterfaceDeclaration parent2 = goUp(c1, i);
		return parent1!=null && parent1.equals(parent2);
	}

	private static ClassOrInterfaceDeclaration goUp(ClassOrInterfaceDeclaration c1, int i) {
		if(i>0) {
			if(!c1.getExtendedTypes().isEmpty()) {
				ClassOrInterfaceDeclaration parent = classes.get(getFullyQualifiedName(c1.getExtendedTypes(0)));
				return goUp(parent, i-1);
			} else return null;
		}
		return c1;
	}

	private static boolean isAncestor(ClassOrInterfaceDeclaration c1, ClassOrInterfaceDeclaration c2) {
		if(!c1.getExtendedTypes().isEmpty()) {
			ClassOrInterfaceDeclaration parent = classes.get(getFullyQualifiedName(c1.getExtendedTypes(0)));
			if(isSuperClass(parent, c2))
				return true;
			else return isAncestor(parent, c2);
		}
		return false;
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

	private static String getFullyQualifiedName(ClassOrInterfaceDeclaration c2) {
		String name = "";
		ClassOrInterfaceDeclaration parentClass = c2.getParentNode().isPresent() ? getClass(c2.getParentNode().get()): null;
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

	public static NodeLocation getLocation(Sequence clone) {
		List<NodeLocation> locations = new ArrayList<>();
		for(int i = 0; i<clone.getSequence().get(0).getContents().getNodes().size(); i++) {
			for(int j = 0; j<clone.getSequence().size(); j++) {
				for(int z = 0; z<clone.getSequence().size(); z++) {
					locations.add(getLocation(clone.getSequence().get(j).getContents().getNodes().get(i), clone.getSequence().get(z).getContents().getNodes().get(i)));
				}
			}
		}
		return locations.stream().sorted().reduce((first, second) -> second).get();
	}
}
