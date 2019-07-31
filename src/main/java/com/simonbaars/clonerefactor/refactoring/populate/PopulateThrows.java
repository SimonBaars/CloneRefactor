package com.simonbaars.clonerefactor.refactoring.populate;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.refactoring.visitor.ThrowsVisitor;

public class PopulateThrows implements PopulatesExtractedMethod {
	@Override
	public void execute(MethodDeclaration extractedMethod) {
		extractedMethod.accept(new ThrowsVisitor(), extractedMethod);
	}
}
