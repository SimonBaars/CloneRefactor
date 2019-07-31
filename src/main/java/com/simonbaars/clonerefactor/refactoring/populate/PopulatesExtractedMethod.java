package com.simonbaars.clonerefactor.refactoring.populate;

import com.github.javaparser.ast.body.MethodDeclaration;

public interface PopulatesExtractedMethod {
	public void execute(MethodDeclaration extractedMethod);
}
