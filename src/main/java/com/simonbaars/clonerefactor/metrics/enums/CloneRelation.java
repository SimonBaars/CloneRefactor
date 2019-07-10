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
import java.util.function.Supplier;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.common.base.Function;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneRelation implements MetricEnum<RelationType> { 
	public enum RelationType { //Please note that the order of these enum constants matters
		SAMEMETHOD,
		SAMECLASS,
		SUPERCLASS,
		ANCESTOR,
		SIBLING,
		FIRSTCOUSIN,
		COMMONHIERARCHY,
		EXTERNALSUPERCLASS,
		SAMEINTERFACE,
		UNRELATED
	}
	
	private final Map<String, ClassOrInterfaceDeclaration> classes = new HashMap<>();
	
	public CloneRelation() {}
	
	public RelationType getLocation(Node n1, Node n2) {
		ClassOrInterfaceDeclaration c1 = getClass(n1);
		ClassOrInterfaceDeclaration c2 = getClass(n2);
		if(c1 == null || c2 == null || c1.isInterface() || c2.isInterface())
			return UNRELATED;
		if(getFullyQualifiedName(c1).equals(getFullyQualifiedName(c2))) {
			if(isMethod(n1, n2))
				return SAMEMETHOD;
			return SAMECLASS;
		}
		if(isSuperClass(c1, c2) || isSuperClass(c2, c1)) 
			return SUPERCLASS;
		if(isAncestor(c1,c2) || isAncestor(c2,c1))
			return ANCESTOR;
		if(isSiblingOrCousin(c1, c2, 1, 1))
			return SIBLING;
		if(isSiblingOrCousin(c1, c2, 2, 2))
			return FIRSTCOUSIN;
		if(hasExternalSuperclass(c1, c2))
			return EXTERNALSUPERCLASS;
		if(inSameHierarchy(c1,c2))
			return COMMONHIERARCHY;
		if(haveSameInterface(c1, c2))
			return SAMEINTERFACE;
		return UNRELATED;
	}
	
	private boolean haveSameInterface(ClassOrInterfaceDeclaration c1, ClassOrInterfaceDeclaration c2) {
		List<String> classesInHierarchy = new ArrayList<>();
		collectSuperclasses(c1, classesInHierarchy, c1::getImplementedTypes, c1::getImplementedTypes);
		return collectSuperclasses(c2, classesInHierarchy, c2::getImplementedTypes, c2::getImplementedTypes);
	}

	private boolean inSameHierarchy(ClassOrInterfaceDeclaration c1, ClassOrInterfaceDeclaration c2) {
		List<String> classesInHierarchy = new ArrayList<>();
		collectSuperclasses(c1, classesInHierarchy, c1::getExtendedTypes, c1::getExtendedTypes);
		return collectSuperclasses(c2, classesInHierarchy, c2::getExtendedTypes, c2::getExtendedTypes);
	}
	
	private boolean collectSuperclasses(ClassOrInterfaceDeclaration c2, List<String> classesInHierarchy, Supplier<NodeList<ClassOrInterfaceType>> getTypes, Function<Integer, ClassOrInterfaceType> getType) {
		String className = getFullyQualifiedName(c2);
		if(classesInHierarchy.contains(className))
			return true;
		classesInHierarchy.add(className);
		if(!getTypes.get().isEmpty()) {
			String fullyQualifiedName = getFullyQualifiedName(getType.apply(0));
			if(classes.containsKey(fullyQualifiedName)) {
				ClassOrInterfaceDeclaration superClass = classes.get(fullyQualifiedName);
				return collectSuperclasses(superClass, classesInHierarchy, superClass::getExtendedTypes, superClass::getExtendedTypes);
			}
		}
		return false;
	}

	private boolean hasExternalSuperclass(ClassOrInterfaceDeclaration c1, ClassOrInterfaceDeclaration c2) {
		if(c1.getExtendedTypes().isEmpty() || c2.getExtendedTypes().isEmpty())
			return false;
		ClassOrInterfaceType superclassC1 = c1.getExtendedTypes().get(0);
		ClassOrInterfaceType superclassC2 = c2.getExtendedTypes().get(0);
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

	private boolean isSiblingOrCousin(ClassOrInterfaceDeclaration c1, ClassOrInterfaceDeclaration c2, int c1GoUp, int c2GoUp) {
		ClassOrInterfaceDeclaration parent1 = goUp(c1, c1GoUp);
		ClassOrInterfaceDeclaration parent2 = goUp(c2, c2GoUp);
		return parent1!=null && getFullyQualifiedName(parent1).equals(getFullyQualifiedName(parent2));
	}
	
	private ClassOrInterfaceDeclaration goUp(ClassOrInterfaceDeclaration c1, int i) {
		if(i>0 && !c1.getExtendedTypes().isEmpty()) {
			String fullyQualifiedName = getFullyQualifiedName(c1.getExtendedTypes(0));
			if(classes.containsKey(fullyQualifiedName))
				return goUp(classes.get(fullyQualifiedName), i-1);
		}
		return c1;
	}

	private boolean isAncestor(ClassOrInterfaceDeclaration c1, ClassOrInterfaceDeclaration c2) {
		if(!c1.getExtendedTypes().isEmpty()) {
			String fullyQualifiedName = getFullyQualifiedName(c1.getExtendedTypes(0));
			if(!classes.containsKey(fullyQualifiedName))
				return false;
			ClassOrInterfaceDeclaration parent = classes.get(fullyQualifiedName);
			if(isSuperClass(parent, c2))
				return true;
			else return isAncestor(parent, c2);
		}
		return false;
	}

	private boolean isMethod(Node n1, Node n2) {
		MethodDeclaration m1 = getMethod(n1);
		MethodDeclaration m2 = getMethod(n2);
		return m1!=null && m1.equals(m2);
	}

	private boolean isSuperClass(ClassOrInterfaceDeclaration c1, ClassOrInterfaceDeclaration c2) {
		return c1.getExtendedTypes().stream().anyMatch(e -> {
			String fullyQualifiedName = getFullyQualifiedName(e);
			if(!classes.containsKey(fullyQualifiedName))
				return false;
			return getFullyQualifiedName(c2).equals(fullyQualifiedName);
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
