package com.simonbaars.clonerefactor.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		System.out.println(n1+" vs "+n2);
		ClassOrInterfaceDeclaration c1 = getClass(n1);
		ClassOrInterfaceDeclaration c2 = getClass(n2);
		if(c1 == null || c2 == null)
			return UNRELATED;
		System.out.println(getFullyQualifiedName(c1)+", "+getFullyQualifiedName(c2));
		if(c1!=null && getFullyQualifiedName(c1).equals(getFullyQualifiedName(c2))) {
			if(isMethod(n1, n2))
				return SAMEMETHOD;
			return SAMECLASS;
		}
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

	private static boolean isSiblingOrCousin(ClassOrInterfaceDeclaration c1, ClassOrInterfaceDeclaration c2, int c1GoUp, int c2GoUp) {
		ClassOrInterfaceDeclaration parent1 = goUp(c1, c1GoUp);
		ClassOrInterfaceDeclaration parent2 = goUp(c1, c2GoUp);
		System.out.println("Sibling check "+i+" = "+getFullyQualifiedName(parent1)+" vs "+getFullyQualifiedName(parent2));
		return parent1!=null && getFullyQualifiedName(parent1).equals(getFullyQualifiedName(parent2));
	}

	private static ClassOrInterfaceDeclaration goUp(ClassOrInterfaceDeclaration c1, int i) {
		if(i>0) {
			if(!c1.getExtendedTypes().isEmpty()) {
				String fullyQualifiedName = getFullyQualifiedName(c1, c1.getExtendedTypes(0));
				System.out.println("goUp "+fullyQualifiedName);
				if(classes.containsKey(fullyQualifiedName)) {
					ClassOrInterfaceDeclaration parent = classes.get(fullyQualifiedName);
					return goUp(parent, i-1);
				} else return null;
			} else return null;
		}
		return c1;
	}

	private static boolean isAncestor(ClassOrInterfaceDeclaration c1, ClassOrInterfaceDeclaration c2) {
		if(!c1.getExtendedTypes().isEmpty()) {
			String fullyQualifiedName = getFullyQualifiedName(c1, c1.getExtendedTypes(0));
			if(!classes.containsKey(fullyQualifiedName))
				return false;
			ClassOrInterfaceDeclaration parent = classes.get(fullyQualifiedName);
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
		return c1.getExtendedTypes().stream().anyMatch(e -> {
			String fullyQualifiedName = getFullyQualifiedName(c2, e);
			if(!classes.containsKey(fullyQualifiedName))
				return false;
			return getFullyQualifiedName(c2).equals(fullyQualifiedName);
		});
	}

	private static String getFullyQualifiedName(ClassOrInterfaceDeclaration n, ClassOrInterfaceType t) {
		String name = "";
		if(t.getScope().isPresent())
			name+=getFullyQualifiedName(n, t.getScope().get())+".";
		else if(n!=null) {
			Optional<String> nameOpt = getCompilationUnit(n).getImports().stream().map(e -> e.getNameAsString()).filter(e -> e.endsWith("."+t.getName())).findAny();
			if(nameOpt.isPresent()) //TODO: Parse import with asterisk in the else clause.
				return nameOpt.get();
			else {
				String fullyQualifiedName = getFullyQualifiedName(n);
				name+=fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf('.')+1);
			}
		}
		return name+t.getNameAsString();
	}

	private static String getFullyQualifiedName(ClassOrInterfaceDeclaration c2) {
		String name = "";
		if(c2 == null)
			return null;
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
		System.out.println("CLONE "+clone);
		for(int i = 0; i<clone.getSequence().get(0).getContents().getNodes().size(); i++) {
			for(int j = 0; j<clone.getSequence().size(); j++) {
				for(int z = j+1; z<clone.getSequence().size(); z++) {
					locations.add(getLocation(clone.getSequence().get(j).getContents().getNodes().get(i), clone.getSequence().get(z).getContents().getNodes().get(i)));
					System.out.println(locations.get(locations.size()-1));
				}
			}
		}
		System.out.println(Arrays.toString(locations.toArray()));
		return locations.stream().sorted().reduce((first, second) -> second).get();
	}
}
