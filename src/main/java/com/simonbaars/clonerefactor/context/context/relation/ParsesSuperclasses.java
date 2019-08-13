package com.simonbaars.clonerefactor.context.context.relation;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

public interface ParsesSuperclasses extends ResolvesFullyQualifiedIdentifiers, ConvertsToOptional {
	public default Optional<ClassOrInterfaceDeclaration[]> collectSuperclasses(Map<String, ClassOrInterfaceDeclaration> classes, ClassOrInterfaceDeclaration classDecl, List<String> classesInHierarchy, Supplier<NodeList<ClassOrInterfaceType>> getTypes, TriFunction<Map<String, ClassOrInterfaceDeclaration>, ClassOrInterfaceDeclaration, List<String>, Optional<ClassOrInterfaceDeclaration[]>> recurse, boolean isInterface) {
		String className = getFullyQualifiedName(classDecl);
		if(classesInHierarchy.contains(className) && classDecl.isInterface() == isInterface)
			return uses(classDecl);
		classesInHierarchy.add(className);
		for(ClassOrInterfaceType type : getTypes.get()) {
			String fullyQualifiedName = getFullyQualifiedName(type);
			if(classes.containsKey(fullyQualifiedName)) {
				ClassOrInterfaceDeclaration superClass = classes.get(fullyQualifiedName);
				return recurse.apply(classes, superClass, classesInHierarchy);
			}
		}
		return Optional.empty();
	}
}
