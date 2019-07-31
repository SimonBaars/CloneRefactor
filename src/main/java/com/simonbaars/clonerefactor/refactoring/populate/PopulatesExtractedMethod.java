package com.simonbaars.clonerefactor.refactoring.populate;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.model.Sequence;

public interface PopulatesExtractedMethod {
	public void execute(MethodDeclaration extractedMethod, Sequence sequence);
}
