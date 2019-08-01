package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;

public interface PopulatesExtractedMethod {
	public void prePopulate(MethodDeclaration extractedMethod, List<Node> topLevel);
	public void modifyMethodCall(MethodCallExpr expr);
	public void postPopulate(MethodDeclaration extractedMethod);
}
