package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.simonbaars.clonerefactor.refactoring.visitor.ThrowsVisitor;

public class PopulateThrows implements PopulatesExtractedMethod {
	@Override
	public void prePopulate(MethodDeclaration extractedMethod, List<Node> topLevel) {
		// Does not pre populate
	}

	@Override
	public Optional<Statement> modifyMethodCall(MethodCallExpr expr) {
		return Optional<T>.empty();
	}

	@Override
	public void postPopulate(MethodDeclaration extractedMethod) {
		extractedMethod.accept(new ThrowsVisitor(), extractedMethod);
	}
}
