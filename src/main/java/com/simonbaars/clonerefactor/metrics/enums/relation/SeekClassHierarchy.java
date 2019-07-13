package com.simonbaars.clonerefactor.metrics.enums.relation;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.metrics.model.ComparingClasses;

public interface SeekClassHierarchy extends ParsesSuperclasses {
	public default boolean inSameHierarchy(ComparingClasses cc) {
		List<String> classesInHierarchy = new ArrayList<>();
		collectSuperclasses(cc.getClassOne(), classesInHierarchy);
		return collectSuperclasses(cc.getClassTwo(), classesInHierarchy);
	}
	
	public default boolean collectSuperclasses(ClassOrInterfaceDeclaration classDecl, List<String> classesInHierarchy) {
		return collectSuperclasses(classDecl, classesInHierarchy, classDecl::getExtendedTypes, this::collectSuperclasses, false);
	}
}
