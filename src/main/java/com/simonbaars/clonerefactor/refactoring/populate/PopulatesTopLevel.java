package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;

public interface PopulatesTopLevel {
	public void execute(MethodDeclaration extractedMethod, List<Node> topLevel);
}
