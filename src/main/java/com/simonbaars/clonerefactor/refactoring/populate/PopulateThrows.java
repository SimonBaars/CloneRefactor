package com.simonbaars.clonerefactor.refactoring.populate;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.refactoring.visitor.ThrowsVisitor;

public class PopulateThrows implements PopulatesExtractedMethod {

	@Override
	public void execute(MethodDeclaration extractedMethod, Sequence sequence) {
		execute(extractedMethod, sequence.getAny());
	}
	
	public void execute(MethodDeclaration extractedMethod, Location loc) {
		extractedMethod.accept(new ThrowsVisitor(), extractedMethod);
	}
}
