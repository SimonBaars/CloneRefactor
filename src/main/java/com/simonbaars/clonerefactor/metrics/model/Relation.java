package com.simonbaars.clonerefactor.metrics.model;

import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.metrics.context.CloneRelation.RelationType;

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
		if(type != null){
			Optional<ClassOrInterfaceDeclaration> result = fetchRelation.get();
			if(result.isPresent()) {
				this.type = type;
				this.intersectingClass = result.get();
			}
		}
	}
}
