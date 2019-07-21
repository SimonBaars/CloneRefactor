package com.simonbaars.clonerefactor.metrics.model;

import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.metrics.context.enums.RelationType;

public class Relation {
	private RelationType type;
	private ClassOrInterfaceDeclaration intersectingClass;
	
	public Relation() {
		super();
	}
	
	public Relation(RelationType type, ClassOrInterfaceDeclaration intersectingClass) {
		super();
		this.type = type;
		this.intersectingClass = intersectingClass;
	}

	public RelationType getType() {
		return type;
	}
	
	public ClassOrInterfaceDeclaration getIntersectingClass() {
		return intersectingClass;
	}
	
	public void setRelationIfNotYetDetermined(RelationType type, Supplier<Optional<ClassOrInterfaceDeclaration>> fetchRelation) {
		if(this.type == null){
			Optional<ClassOrInterfaceDeclaration> result = fetchRelation.get();
			if(result.isPresent()) {
				this.type = type;
				this.intersectingClass = result.get();
			}
		}
	}

	public void unrelated(boolean isExternal) {
		this.type = isExternal ? RelationType.EXTERNALSUPERCLASS : RelationType.UNRELATED;
	}

	@Override
	public String toString() {
		return "Relation [type=" + type + ", intersectingClass=" + intersectingClass + "]";
	}

	public boolean isSameClass() {
		return type == RelationType.SAMECLASS || type == RelationType.SAMEMETHOD;
	}

	public boolean isEffectivelyUnrelated() {
		return type == RelationType.UNRELATED || type == RelationType.EXTERNALSUPERCLASS || type == RelationType.NODIRECTSUPERCLASS || type == RelationType.NOINDIRECTSUPERCLASS;
	}

	public void setIntersectingClass(ClassOrInterfaceDeclaration addInterface) {
		this.intersectingClass = addInterface;
	}
}
