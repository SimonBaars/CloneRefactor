package com.simonbaars.clonerefactor.metrics.context.analyze;

import static com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation.RelationType.ANCESTOR;
import static com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation.RelationType.COMMONHIERARCHY;
import static com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation.RelationType.FIRSTCOUSIN;
import static com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation.RelationType.SAMECLASS;
import static com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation.RelationType.SAMEINTERFACE;
import static com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation.RelationType.SAMEMETHOD;
import static com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation.RelationType.SIBLING;
import static com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation.RelationType.SUPERCLASS;
import static com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation.RelationType.UNRELATED;
import static com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation.RelationType.NODIRECTSUPERCLASS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.simonbaars.clonerefactor.metrics.context.interfaces.MetricEnum;
import com.simonbaars.clonerefactor.metrics.context.relation.SeekClassHierarchy;
import com.simonbaars.clonerefactor.metrics.context.relation.SeekInterfaceHierarchy;
import com.simonbaars.clonerefactor.metrics.model.ComparingClasses;
import com.simonbaars.clonerefactor.metrics.model.Relation;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneRelation implements MetricEnum<Relation>, SeekClassHierarchy, SeekInterfaceHierarchy { 
	public enum RelationType { //Please note that the order of these enum constants matters
		SAMEMETHOD, // Refactor to same class as a private method
		SAMECLASS, // Refactor to same class as a private method
		SUPERCLASS, // Refactor to topmost class as a protected method
		ANCESTOR, // Refactor to common parent class as a protected method
		SIBLING, // Refactor to common parent class as a protected method
		FIRSTCOUSIN, // Refactor to common parent class as a protected method
		COMMONHIERARCHY, // Refactor to common parent class as a protected method
		NODIRECTSUPERCLASS, // Refactor to newly created abstract class as a protected method
		SAMEINTERFACE, // Refactor common interface as an default method
		EXTERNALSUPERCLASS, // Refactor to newly created interface as a default method
		UNRELATED // Refactor to newly created interface as a default method
	}
	
	private final Map<String, ClassOrInterfaceDeclaration> classes = new HashMap<>();
	
	public CloneRelation() {}
	
	public Relation getLocation(Node n1, Node n2) {
		Optional<ClassOrInterfaceDeclaration> class1 = getClass(n1);
		Optional<ClassOrInterfaceDeclaration> class2 = getClass(n2);
		
		if(!class1.isPresent() || !class2.isPresent())
			return new Relation(UNRELATED, null);
		
		ComparingClasses cc = new ComparingClasses(class1.get(), class2.get());
		ComparingClasses rev = cc.reverse();
		final Relation relation = new Relation();
		relation.setRelationIfNotYetDetermined(SAMECLASS, () -> isSameClass(cc));
		relation.setRelationIfNotYetDetermined(SAMEMETHOD, () -> isMethod(cc, n1, n2), true);
		relation.setRelationIfNotYetDetermined(SUPERCLASS, () -> isSuperClass(cc));
		relation.setRelationIfNotYetDetermined(SUPERCLASS, () -> isSuperClass(rev));
		relation.setRelationIfNotYetDetermined(ANCESTOR, () -> isAncestor(cc));
		relation.setRelationIfNotYetDetermined(ANCESTOR, () -> isAncestor(rev));
		relation.setRelationIfNotYetDetermined(SIBLING, () -> isSibling(cc));
		relation.setRelationIfNotYetDetermined(FIRSTCOUSIN, () -> isFirstCousin(cc));
		relation.setRelationIfNotYetDetermined(COMMONHIERARCHY, () -> sameHierarchy(classes, cc));
		relation.setRelationIfNotYetDetermined(NODIRECTSUPERCLASS, () -> noSuperclass(cc));
		relation.setRelationIfNotYetDetermined(SAMEINTERFACE, () -> sameInterface(classes, cc));
		if(relation.getType() == null)
			relation.unrelated(hasExternalSuperclass(cc));
		return relation;
	}

	private Optional<ClassOrInterfaceDeclaration> noSuperclass(ComparingClasses cc) {
		return cc.getClassOne().getExtendedTypes().isEmpty() && cc.getClassTwo().getExtendedTypes().isEmpty() ? Optional.of(cc.getClassOne()) : Optional.empty();
	}

	private Optional<ClassOrInterfaceDeclaration> isSameClass(ComparingClasses cc) {
		if(cc.getClassOne()  == cc.getClassTwo())
			return Optional.of(cc.getClassOne());
		return Optional.empty();
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
	
	private Optional<ClassOrInterfaceDeclaration> isSibling(ComparingClasses cc){
		return isSiblingOrCousin(cc, 1, 1);
	}
	
	private Optional<ClassOrInterfaceDeclaration> isFirstCousin(ComparingClasses cc){
		return isSiblingOrCousin(cc, 2, 2);
	}

	private Optional<ClassOrInterfaceDeclaration> isSiblingOrCousin(ComparingClasses cc, int c1GoUp, int c2GoUp) {
		ClassOrInterfaceDeclaration parent1 = goUp(cc.getClassOne(), c1GoUp);
		ClassOrInterfaceDeclaration parent2 = goUp(cc.getClassTwo(), c2GoUp);
		return parent1 == parent2 ? Optional.of(parent1) : Optional.empty();
	}
	
	private ClassOrInterfaceDeclaration goUp(ClassOrInterfaceDeclaration classDecl, int i) {
		if(i>0 && !classDecl.getExtendedTypes().isEmpty()) {
			String fullyQualifiedName = getFullyQualifiedName(classDecl.getExtendedTypes(0));
			if(classes.containsKey(fullyQualifiedName))
				return goUp(classes.get(fullyQualifiedName), i-1);
		}
		return classDecl;
	}

	private Optional<ClassOrInterfaceDeclaration> isAncestor(ComparingClasses cc) {
		if(!cc.getClassOne().getExtendedTypes().isEmpty()) {
			String fullyQualifiedName = getFullyQualifiedName(cc.getClassOne().getExtendedTypes(0));
			if(!classes.containsKey(fullyQualifiedName))
				return Optional.empty();
			ComparingClasses superCC = new ComparingClasses(classes.get(fullyQualifiedName), cc.getClassTwo());
			
			Optional<ClassOrInterfaceDeclaration> superclass = isSuperClass(superCC);
			if(superclass.isPresent())
				return superclass;
			else return isAncestor(superCC);
		}
		return Optional.empty();
	}

	private Optional<ClassOrInterfaceDeclaration> isMethod(ComparingClasses cc, Node n1, Node n2) {
		Optional<MethodDeclaration> m1 = getMethod(n1);
		if(m1.isPresent()) {
			Optional<MethodDeclaration> m2 = getMethod(n2);
			if(m2.isPresent() && m1.get() == m2.get())
				return Optional.of(cc.getClassOne());
		}
		return Optional.empty();
	}

	private Optional<ClassOrInterfaceDeclaration> isSuperClass(ComparingClasses cc) {
		return cc.getClassOne().getExtendedTypes().stream().filter(e -> {
			String fullyQualifiedName = getFullyQualifiedName(e);
			if(!classes.containsKey(fullyQualifiedName))
				return false;
			return getFullyQualifiedName(cc.getClassTwo()).equals(fullyQualifiedName);
		}).map(e -> cc.getClassTwo()).findAny();
	}

	@Override
	public Relation get(Sequence clone) {
		List<Relation> locations = new ArrayList<>();
		for(int i = 0; i<clone.getLocations().get(0).getContents().getNodes().size(); i++) {
			for(int j = 0; j<clone.getLocations().size(); j++) {
				for(int z = j+1; z<clone.getLocations().size(); z++) {
					if(clone.getLocations().get(j).getContents().getNodes().size()<=i || clone.getLocations().get(z).getContents().getNodes().size()<=i)
						continue;
					locations.add(getLocation(clone.getLocations().get(j).getContents().getNodes().get(i), clone.getLocations().get(z).getContents().getNodes().get(i)));
				}
			}
		}
		return locations.stream().reduce((first, second) -> first.getType().compareTo(second.getType()) < 0 ? first : second).get();
	}
}
