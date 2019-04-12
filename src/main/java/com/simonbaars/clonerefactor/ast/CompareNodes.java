package com.simonbaars.clonerefactor.ast;

import java.util.stream.IntStream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.simonbaars.clonerefactor.model.CompareRules;

public class CompareNodes {
	private CompareRules rules;
	
	public CompareNodes(CompareRules rules) {
		super();
		this.rules = rules;
	}

	public boolean compare(Node n1, Node n2) {
		if(n1.getClass() != n2.getClass())
			return false;
		if(n1 instanceof MethodDeclaration)
			compareMethodDeclarations((MethodDeclaration)n1, (MethodDeclaration)n2);
		return n1.equals(n2);
	}

	private boolean compareMethodDeclarations(MethodDeclaration n1, MethodDeclaration n2) {
		if(rules.isCheckMethodNames() && !n1.getNameAsString().equals(n2.getNameAsString()))
			return false;
		return n1.getParameters().size() == n2.getParameters().size() &&
				IntStream.range(0, n1.getParameters().size()).boxed().allMatch(i -> compareParameters(n1.getParameters().get(i), n2.getParameters().get(i)));
	}

	private boolean compareParameters(Parameter p1, Parameter p2) {
		if(rules.isCheckParameterNames() && !p1.getNameAsString().equals(p2.getNameAsString()))
			return false;
		return p1.getType() == p2.getType();
	}
}
