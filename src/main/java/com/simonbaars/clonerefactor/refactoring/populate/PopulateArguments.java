package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.Statement;
import com.simonbaars.clonerefactor.ast.resolution.ResolvedVariable;
import com.simonbaars.clonerefactor.refactoring.visitor.DeclaresVariableVisitor;
import com.simonbaars.clonerefactor.refactoring.visitor.VariableVisitor;

public class PopulateArguments implements PopulatesExtractedMethod {
	final Map<SimpleName, ResolvedVariable> usedVariables = new HashMap<>();
	Map<String, ClassOrInterfaceDeclaration> classes;
	
	public PopulateArguments() {}
	
	@Override
	public void prePopulate(MethodDeclaration extractedMethod, List<Node> topLevel) {
		topLevel.forEach(n -> n.accept(new VariableVisitor(classes), usedVariables));
		for(Entry<SimpleName, ResolvedVariable> var : usedVariables.entrySet()) {
			if(declaresVariable(extractedMethod, var.getKey())) {
				extractedMethod.addParameter(var.getValue().getType(), var.getKey().asString());
			}
		}
	}

	@Override
	public Optional<Statement> modifyMethodCall(MethodCallExpr expr) {
		usedVariables.keySet().forEach(v -> expr.addArgument(v.toString()));
		return Optional.empty();
	}

	@Override
	public void postPopulate(MethodDeclaration extractedMethod) {
		usedVariables.clear();
	}

	private boolean declaresVariable(MethodDeclaration extractedMethod, SimpleName varName) {
		Boolean b = extractedMethod.accept(new DeclaresVariableVisitor(), varName);
		if(b == null)
			return false;
		return b;
	}

	public void setClasses(Map<String, ClassOrInterfaceDeclaration> classes) {
		this.classes = classes;
	}

}
