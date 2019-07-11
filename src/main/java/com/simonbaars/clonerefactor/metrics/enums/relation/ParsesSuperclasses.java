package com.simonbaars.clonerefactor.metrics.enums.relation;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.simonbaars.clonerefactor.metrics.enums.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.metrics.enums.ClassOrInterfaceType;
import com.simonbaars.clonerefactor.metrics.enums.NodeList;

public interface ParsesSuperclasses {
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
}
