package com.simonbaars.clonerefactor.context.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.context.model.ComparingClasses;

public interface SeekClassHierarchy extends ParsesSuperclasses {
	public default Optional<ClassOrInterfaceDeclaration[]> sameHierarchy(Map<String, ClassOrInterfaceDeclaration> classes, ComparingClasses cc) {
		List<String> classesInHierarchy = new ArrayList<>();
		collectSuperclasses(classes, cc.getClassOne(), classesInHierarchy);
		return collectSuperclasses(classes, cc.getClassTwo(), classesInHierarchy);
	}
	
	public default Optional<ClassOrInterfaceDeclaration[]> collectSuperclasses(Map<String, ClassOrInterfaceDeclaration> classes, ClassOrInterfaceDeclaration classDecl, List<String> classesInHierarchy) {
		return collectSuperclasses(classes, classDecl, classesInHierarchy, classDecl::getExtendedTypes, this::collectSuperclasses, false);
	}
}
