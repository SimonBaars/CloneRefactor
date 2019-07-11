package com.simonbaars.clonerefactor.metrics.enums;

import static com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType.ANCESTOR;
import static com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType.COMMONHIERARCHY;
import static com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType.EXTERNALSUPERCLASS;
import static com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType.FIRSTCOUSIN;
import static com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType.SAMECLASS;
import static com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType.SAMEINTERFACE;
import static com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType.SAMEMETHOD;
import static com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType.SIBLING;
import static com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType.SUPERCLASS;
import static com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType.UNRELATED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.metrics.model.ComparingClasses;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneRelation implements MetricEnum<RelationType> { 
	public enum RelationType { //Please note that the order of these enum constants matters
		SAMEMETHOD, // Refactor to same class
		SAMECLASS, // Refactor to same class
		SUPERCLASS, // Refactor to topmost class
		ANCESTOR, // Refactor to common parent class
		SIBLING, // Refactor to common parent class
		FIRSTCOUSIN, // Refactor to common parent class
		COMMONHIERARCHY, // Refactor to common parent class
		SAMEINTERFACE, // Refactor common interface as an default method
		EXTERNALSUPERCLASS, // Refactor to newly created interface as a default method
		UNRELATED // Refactor to newly created interface as a default method
	}
	
	private final Map<String, ClassOrInterfaceDeclaration> classes = new HashMap<>();
	
	public CloneRelation() {}
	
	public RelationType getLocation(Node n1, Node n2) {
		ComparingClasses cc = new ComparingClasses(getClass(n1), getClass(n2));
		ComparingClasses rev = cc.reverse();
		if(cc.invalid())
			return UNRELATED;
		if(getFullyQualifiedName(cc.getClassOne()).equals(getFullyQualifiedName(cc.getClassTwo()))) {
			if(isMethod(n1, n2))
				return SAMEMETHOD;
			return SAMECLASS;
		}
		if(isSuperClass(cc) || isSuperClass(rev)) 
			return SUPERCLASS;
		if(isAncestor(cc) || isAncestor(rev))
			return ANCESTOR;
		if(isSiblingOrCousin(cc, 1, 1))
			return SIBLING;
		if(isSiblingOrCousin(cc, 2, 2))
			return FIRSTCOUSIN;
		if(inSameHierarchy(cc))
			return COMMONHIERARCHY;
		if(haveSameInterface(cc))
			return SAMEINTERFACE;
		if(hasExternalSuperclass(cc))
			return EXTERNALSUPERCLASS;
		return UNRELATED;
	}

	private boolean haveSameInterface(ComparingClasses cc) {
		List<String> classesInHierarchy = new ArrayList<>();
		collectInterfaces(cc.getClassOne(), classesInHierarchy);
		return collectInterfaces(cc.getClassTwo(), classesInHierarchy);
	}

	private boolean inSameHierarchy(ComparingClasses cc) {
		List<String> classesInHierarchy = new ArrayList<>();
		collectSuperclasses(cc.getClassOne(), classesInHierarchy);
		return collectSuperclasses(cc.getClassTwo(), classesInHierarchy);
	}
	
	private boolean collectSuperclasses(ClassOrInterfaceDeclaration classDecl, List<String> classesInHierarchy) {
		return collectSuperclasses(classDecl, classesInHierarchy, classDecl::getExtendedTypes, this::collectSuperclasses, false);
	}
	
	private boolean collectInterfaces(ClassOrInterfaceDeclaration classDecl, List<String> classesInHierarchy) {
		if(collectSuperclasses(classDecl, classesInHierarchy, classDecl::getExtendedTypes, this::collectInterfaces, true))
			return true;
		return collectSuperclasses(classDecl, classesInHierarchy, classDecl::getImplementedTypes, this::collectInterfaces, true);
	}
	
	private boolean collectSuperclasses(ClassOrInterfaceDeclaration classDecl, List<String> classesInHierarchy, Supplier<NodeList<ClassOrInterfaceType>> getTypes, BiFunction<ClassOrInterfaceDeclaration, List<String>, Boolean> recurse, boolean isInterface) {
		String className = getFullyQualifiedName(classDecl);
		if(classesInHierarchy.contains(className) && classDecl.isInterface() == isInterface)
			return true;
		classesInHierarchy.add(className);
		for(ClassOrInterfaceType type : getTypes.get()) {
			String fullyQualifiedName = getFullyQualifiedName(type);
			if(classes.containsKey(fullyQualifiedName)) {
				ClassOrInterfaceDeclaration superClass = classes.get(fullyQualifiedName);
				return recurse.apply(superClass, classesInHierarchy);
			}
		}
		return false;
	}

	private boolean hasExternalSuperclass(ComparingClasses cc) {
		if(!cc.hasExtendedTypes())
			return false;
		ClassOrInterfaceType superclassC1 = cc.getClassOne().getExtendedTypes().get(0);
		ClassOrInterfaceType superclassC2 = cc.getClassTwo().getExtendedTypes().get(0);
		return !superclassC1.getNameAsString().equals("Object") && 
			   !superclassC2.getNameAsString().equals("Object") && 
				getFullyQualifiedName(superclassC1).equals(getFullyQualifiedName(superclassC2));
	}

	public void registerNode(Node n) {
		if(n instanceof ClassOrInterfaceDeclaration) {
			ClassOrInterfaceDeclaration n2 = (ClassOrInterfaceDeclaration)n;
			classes.put(getFullyQualifiedName(n2), n2);
		}
	}
	
	public void clearClasses() {
		classes.clear();
	}

	private boolean isSiblingOrCousin(ComparingClasses cc, int c1GoUp, int c2GoUp) {
		ClassOrInterfaceDeclaration parent1 = goUp(cc.getClassOne(), c1GoUp);
		ClassOrInterfaceDeclaration parent2 = goUp(cc.getClassTwo(), c2GoUp);
		return parent1!=null && getFullyQualifiedName(parent1).equals(getFullyQualifiedName(parent2));
	}
	
	private ClassOrInterfaceDeclaration goUp(ClassOrInterfaceDeclaration classDecl, int i) {
		if(i>0 && !classDecl.getExtendedTypes().isEmpty()) {
			String fullyQualifiedName = getFullyQualifiedName(classDecl.getExtendedTypes(0));
			if(classes.containsKey(fullyQualifiedName))
				return goUp(classes.get(fullyQualifiedName), i-1);
		}
		return classDecl;
	}

	private boolean isAncestor(ComparingClasses cc) {
		if(!cc.getClassOne().getExtendedTypes().isEmpty()) {
			String fullyQualifiedName = getFullyQualifiedName(cc.getClassOne().getExtendedTypes(0));
			if(!classes.containsKey(fullyQualifiedName))
				return false;
			ComparingClasses superCC = new ComparingClasses(classes.get(fullyQualifiedName), cc.getClassTwo());
			
			if(isSuperClass(superCC))
				return true;
			else return isAncestor(superCC);
		}
		return false;
	}

	private boolean isMethod(Node n1, Node n2) {
		MethodDeclaration m1 = getMethod(n1);
		MethodDeclaration m2 = getMethod(n2);
		return m1!=null && m1.equals(m2);
	}

	private boolean isSuperClass(ComparingClasses cc) {
		return cc.getClassOne().getExtendedTypes().stream().anyMatch(e -> {
			String fullyQualifiedName = getFullyQualifiedName(e);
			if(!classes.containsKey(fullyQualifiedName))
				return false;
			return getFullyQualifiedName(cc.getClassTwo()).equals(fullyQualifiedName);
		});
	}

	private String getFullyQualifiedName(ClassOrInterfaceType t) {
		try { 
			return t.resolve().getQualifiedName();
		} catch (Exception e) {
			return t.getNameAsString();
		}
	}

	private String getFullyQualifiedName(ClassOrInterfaceDeclaration c2) {
		try { 
			return c2.resolve().getQualifiedName();
		} catch (Exception e) {
			return c2.getNameAsString();
		}
	}

	@Override
	public RelationType get(Sequence clone) {
		List<RelationType> locations = new ArrayList<>();
		for(int i = 0; i<clone.getLocations().get(0).getContents().getNodes().size(); i++) {
			for(int j = 0; j<clone.getLocations().size(); j++) {
				for(int z = j+1; z<clone.getLocations().size(); z++) {
					if(clone.getLocations().get(j).getContents().getNodes().size()<=i || clone.getLocations().get(z).getContents().getNodes().size()<=i)
						continue;
					locations.add(getLocation(clone.getLocations().get(j).getContents().getNodes().get(i), clone.getLocations().get(z).getContents().getNodes().get(i)));
				}
			}
		}
		return locations.stream().sorted().reduce((first, second) -> second).get();
	}
}
