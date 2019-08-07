package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.Statement;
import com.simonbaars.clonerefactor.ast.resolution.ResolvedVariable;
import com.simonbaars.clonerefactor.refactoring.visitor.VariableVisitor;

public class PopulateArguments implements PopulatesExtractedMethod {
	final Map<SimpleName, ResolvedVariable> usedVariables = new LinkedHashMap<>();
	Map<String, ClassOrInterfaceDeclaration> classes;
	
	public PopulateArguments() {}
	
	@Override
	public void prePopulate(MethodDeclaration extractedMethod, List<Node> topLevel) {
		topLevel.forEach(n -> n.accept(new VariableVisitor(classes), usedVariables));
		for(SimpleName varName : new ArrayList<>(usedVariables.keySet())) {
			ResolvedVariable var = usedVariables.get(varName);
			if(!declaresVariable(topLevel, var)) {
				extractedMethod.addParameter(var.getType(), varName.asString());
			} else {
				usedVariables.remove(varName);
			}
		}
	}

	private boolean declaresVariable(List<Node> topLevel, ResolvedVariable value) {
		for(Node node : topLevel) {
			if(node.containsWithin(value.getNode()))
				return true;
		}
		return false;
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

	public void setClasses(Map<String, ClassOrInterfaceDeclaration> classes) {
		this.classes = classes;
	}

}
