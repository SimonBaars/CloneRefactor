package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.refactoring.visitor.DeclaresVariableVisitor;
import com.simonbaars.clonerefactor.refactoring.visitor.VariableVisitor;

public class PopulateArguments implements PopulatesExtractedMethod {
	final Map<NameExpr, ResolvedType> usedVariables = new HashMap<>();
	
	public PopulateArguments() {}
	
	@Override
	public void prePopulate(MethodDeclaration extractedMethod, List<Node> topLevel) {
		topLevel.forEach(n -> n.accept(new VariableVisitor(), usedVariables));
		for(Entry<NameExpr, ResolvedType> var : usedVariables.entrySet()) {
			if(!declaresVariable(extractedMethod, var.getKey())) {
				extractedMethod.addParameter(var.getValue().describe(), var.getKey().getNameAsString());
			}
		}
	}

	@Override
	public void modifyMethodCall(MethodCallExpr expr) {
		usedVariables.keySet().forEach(v -> expr.addArgument(v));
	}

	@Override
	public void postPopulate(MethodDeclaration extractedMethod) {
		usedVariables.clear();
	}

	private boolean declaresVariable(MethodDeclaration extractedMethod, NameExpr varName) {
		Boolean b = extractedMethod.accept(new DeclaresVariableVisitor(), varName);
		if(b == null)
			return false;
		return b;
	}

}
