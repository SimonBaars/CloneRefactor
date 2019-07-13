package com.simonbaars.clonerefactor.metrics.model;

import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.metrics.context.CloneRelation.RelationType;

public class Relation {
	private final RelationType type;
	private final ClassOrInterfaceDeclaration intersectingClass;
	
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
	
	public Relation testRelation(RelationType type, /*BiFunction<ComparingClasses, Map<String, ClassOrInterfaceDeclaration>, Optional<ClassOrInterfaceDeclaration>> fetchRelation*/ Supplier<Optional<ClassOrInterfaceDeclaration>> fetchRelation) {
		Optional<ClassOrInterfaceDeclaration> result;
		if(type != null || !(result = fetchRelation.get()).isPresent())
			return this;
		return new Relation(type, result.get());
	}
}
