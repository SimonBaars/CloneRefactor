package com.simonbaars.clonerefactor.context.relation;

import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.context.model.ComparingClasses;

public interface ConvertsToOptional {
	public default Optional<ClassOrInterfaceDeclaration[]> uses(ClassOrInterfaceDeclaration...decl){
		return Optional.of(decl);
	}
	
	public default Optional<ClassOrInterfaceDeclaration[]> uses(ComparingClasses cc){
		return uses(cc.getClassOne(), cc.getClassTwo());
	}
}
