package com.simonbaars.clonerefactor.metrics.enums.relation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.metrics.model.ComparingClasses;

public interface SeekInterfaceHierarchy extends ParsesSuperclasses {
	public default boolean haveSameInterface(Map<String, ClassOrInterfaceDeclaration> classes, ComparingClasses cc) {
		List<String> classesInHierarchy = new ArrayList<>();
		collectInterfaces(classes, cc.getClassOne(), classesInHierarchy);
		return collectInterfaces(classes, cc.getClassTwo(), classesInHierarchy);
		cc.getClassOne().resolve()
	}
	
	public default boolean collectInterfaces(Map<String, ClassOrInterfaceDeclaration> classes, ClassOrInterfaceDeclaration classDecl, List<String> classesInHierarchy) {
		if(collectSuperclasses(classes, classDecl, classesInHierarchy, classDecl::getExtendedTypes, this::collectInterfaces, true))
			return true;
		return collectSuperclasses(classes, classDecl, classesInHierarchy, classDecl::getImplementedTypes, this::collectInterfaces, true);
	}
}
