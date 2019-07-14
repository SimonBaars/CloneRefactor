package com.simonbaars.clonerefactor.metrics.model;

import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation.RelationType;

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
	
	public void setRelationIfNotYetDetermined(RelationType type, /*BiFunction<ComparingClasses, Map<String, ClassOrInterfaceDeclaration>, Optional<ClassOrInterfaceDeclaration>> fetchRelation*/ Supplier<Optional<ClassOrInterfaceDeclaration>> fetchRelation) {
		setRelationIfNotYetDetermined(type, fetchRelation, false);
	}
	
	public void setRelationIfNotYetDetermined(RelationType type, /*BiFunction<ComparingClasses, Map<String, ClassOrInterfaceDeclaration>, Optional<ClassOrInterfaceDeclaration>> fetchRelation*/ Supplier<Optional<ClassOrInterfaceDeclaration>> fetchRelation, boolean replaceType) {
		if((!replaceType && type == null) || (replaceType && type!=null)){
			Optional<ClassOrInterfaceDeclaration> result = fetchRelation.get();
			if(result.isPresent()) {
				this.type = type;
				if(!replaceType)
					this.intersectingClass = result.get();
			}
		}
	}

	public void unrelated(ClassOrInterfaceDeclaration classOrInterface) {
		this.type = RelationType.UNRELATED;
		this.intersectingClass = classOrInterface;
	}

	@Override
	public String toString() {
		return "Relation [type=" + type + ", intersectingClass=" + intersectingClass + "]";
	}
}
