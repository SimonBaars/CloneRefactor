package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.refactoring.visitor.DeclaresVariableVisitor;
import com.simonbaars.clonerefactor.refactoring.visitor.VariableVisitor;

public class PopulateArguments implements PopulatesTopLevel {
	public PopulateArguments() {}

	@Override
	public void execute(MethodDeclaration extractedMethod, List<Node> topLevelStatements) {
		HashMap<NameExpr, ResolvedType> usedVariables = new HashMap<>();
		topLevelStatements.forEach(n -> n.accept(new VariableVisitor(), usedVariables));
		for(Entry<NameExpr, ResolvedType> var : usedVariables.entrySet()) {
			if(!declaresVariable(extractedMethod, var.getKey())) {
				extractedMethod.addParameter(var.getValue().toString(), var.getKey().getNameAsString());
			}
		}
	}

	private boolean declaresVariable(MethodDeclaration extractedMethod, NameExpr varName) {
		Boolean b = extractedMethod.accept(new DeclaresVariableVisitor(), varName);
		if(b == null)
			return false;
		return b;
	}

}
