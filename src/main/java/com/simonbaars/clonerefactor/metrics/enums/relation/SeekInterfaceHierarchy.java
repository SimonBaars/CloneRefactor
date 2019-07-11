package com.simonbaars.clonerefactor.metrics.enums.relation;

import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.metrics.enums.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.metrics.model.ComparingClasses;

public interface SeekInterfaceHierarchy extends ParsesSuperclasses {
	private boolean haveSameInterface(ComparingClasses cc) {
		List<String> classesInHierarchy = new ArrayList<>();
		collectInterfaces(cc.getClassOne(), classesInHierarchy);
		return collectInterfaces(cc.getClassTwo(), classesInHierarchy);
	}
	
	private boolean collectInterfaces(ClassOrInterfaceDeclaration classDecl, List<String> classesInHierarchy) {
		if(collectSuperclasses(classDecl, classesInHierarchy, classDecl::getExtendedTypes, this::collectInterfaces, true))
			return true;
		return collectSuperclasses(classDecl, classesInHierarchy, classDecl::getImplementedTypes, this::collectInterfaces, true);
	}
}
