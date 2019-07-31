package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.refactoring.visitor.VariableVisitor;

public class PopulateArguments implements PopulatesExtractedMethod {
	public PopulateArguments() {}

	@Override
	public void execute(MethodDeclaration extractedMethod, Sequence sequence) {
		HashMap<NameExpr, ResolvedType> usedVariables = new HashMap<>();
		extractedMethod.accept(new VariableVisitor(), usedVariables);
		for(Entry<NameExpr, ResolvedType> var : usedVariables.entrySet()) {
			if(!declaresVariable(sequence.getAny().getContents().getNodes(), var.getValue())) {
				extractedMethod.addParameter(var.getValue().toString(), var.getKey().getNameAsString());
			}
		}
	}

	private boolean declaresVariable(List<Node> nodes, ResolvedType value) {
		// TODO Auto-generated method stub
		return false;
	}

}
