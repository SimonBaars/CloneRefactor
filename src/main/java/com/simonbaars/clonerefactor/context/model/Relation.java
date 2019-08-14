package com.simonbaars.clonerefactor.context.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.context.enums.RelationType;

public class Relation implements Comparable<Relation> {
	private RelationType type;
	private final List<ClassOrInterfaceDeclaration> intersectingClasses;
	
	public Relation() {
		super();
		intersectingClasses = new ArrayList<>();
	}
	
	public Relation(RelationType type, ClassOrInterfaceDeclaration...intersectingClass) {
		this(type, Arrays.asList(intersectingClass));
	}

	public Relation(RelationType type, List<ClassOrInterfaceDeclaration> intersectingClasses) {
		this.type = type;
		this.intersectingClasses = intersectingClasses;
	}

	public RelationType getType() {
		return type;
	}
	
	public List<ClassOrInterfaceDeclaration> getIntersectingClasses() {
		return intersectingClasses;
	}
	
	public void setRelationIfNotYetDetermined(RelationType type, Supplier<Optional<ClassOrInterfaceDeclaration[]>> fetchRelation) {
		if(this.type == null){
			Optional<ClassOrInterfaceDeclaration[]> result = fetchRelation.get();
			if(result.isPresent()) {
				this.type = type;
				Collections.addAll(intersectingClasses, result.get());
			}
		}
	}
	
	public void merge(Relation p) {
		if(compareTo(p)>0) {
			intersectingClasses.clear();
			type = p.type;
		}
		if(p.type == type) {
			addAll(p);
		}
	}

	private void addAll(Relation p) {
		for(ClassOrInterfaceDeclaration c : p.intersectingClasses) {
			if(intersectingClasses.stream().noneMatch(d -> c == d)) {
				intersectingClasses.add(c);
			}
		}
	}

	public void unrelated(boolean isExternal) {
		this.type = isExternal ? RelationType.EXTERNALSUPERCLASS : RelationType.UNRELATED;
	}

	@Override
	public String toString() {
		return "Relation [type=" + type + ", intersectingClass=" + Arrays.toString(intersectingClasses.toArray()) + "]";
	}

	public boolean isSameClass() {
		return type == RelationType.SAMECLASS || type == RelationType.SAMEMETHOD;
	}

	public boolean isEffectivelyUnrelated() {
		return type == RelationType.EXTERNALANCESTOR || type == RelationType.EXTERNALSUPERCLASS || type == RelationType.NODIRECTSUPERCLASS || type == RelationType.NOINDIRECTSUPERCLASS;
	}

	@Override
	public int compareTo(Relation o) {
		return Integer.compare(type.ordinal(), o.type.ordinal());
	}

	public ClassOrInterfaceDeclaration getFirstClass() {
		return intersectingClasses.get(0);
	}

	public boolean isInterfaceRelation() {
		return type == RelationType.SAMEDIRECTINTERFACE || type == RelationType.SAMEINDIRECTINTERFACE || type == RelationType.EXTERNALANCESTOR || type == RelationType.EXTERNALSUPERCLASS;
	}
	
}
