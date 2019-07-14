package com.simonbaars.clonerefactor.metrics.context.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.metrics.model.ComparingClasses;

public interface SeekInterfaceHierarchy extends ParsesSuperclasses {
	public default Optional<ClassOrInterfaceDeclaration> sameInterface(Map<String, ClassOrInterfaceDeclaration> classes, ComparingClasses cc) {
		List<String> classesInHierarchy = new ArrayList<>();
		collectInterfaces(classes, cc.getClassOne(), classesInHierarchy);
		return collectInterfaces(classes, cc.getClassTwo(), classesInHierarchy);
	}
	
	public default Optional<ClassOrInterfaceDeclaration> collectInterfaces(Map<String, ClassOrInterfaceDeclaration> classes, ClassOrInterfaceDeclaration classDecl, List<String> classesInHierarchy) {
		Optional<ClassOrInterfaceDeclaration> result = collectSuperclasses(classes, classDecl, classesInHierarchy, classDecl::getExtendedTypes, this::collectInterfaces, true);
		if(result.isPresent()) return result;
		return collectSuperclasses(classes, classDecl, classesInHierarchy, classDecl::getImplementedTypes, this::collectInterfaces, true);
	}
}
