package com.simonbaars.clonerefactor.metrics.enums.relation;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.common.base.Optional;

public interface ParsesSuperclasses extends ResolvesFullyQualifiedIdentifiers {
	public default Optional<ClassOrInterfaceDeclaration> collectSuperclasses(ClassOrInterfaceDeclaration classDecl, List<String> classesInHierarchy, Supplier<NodeList<ClassOrInterfaceType>> getTypes, BiFunction<ClassOrInterfaceDeclaration, List<String>, Boolean> recurse, boolean isInterface) {
		String className = getFullyQualifiedName(classDecl);
		if(classesInHierarchy.contains(className) && classDecl.isInterface() == isInterface)
			return Optional.of(classDecl);
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
}
