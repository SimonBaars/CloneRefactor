package com.simonbaars.clonerefactor.metrics.context.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.Type;
import com.simonbaars.clonerefactor.refactoring.visitor.ReturnVariablesCollector;

public interface ChecksReturningData {
	public default List<VariableDeclarationExpr> getTopLevelDeclarators(List<Node> topLevel){
		return topLevel.stream().filter(e -> e instanceof ExpressionStmt && ((ExpressionStmt)e).getExpression() instanceof VariableDeclarationExpr).map(e -> (VariableDeclarationExpr)((ExpressionStmt)e).getExpression()).collect(Collectors.toList());
	}
	
	public default Map<SimpleName, Type> getUsedVariables(List<Node> topLevel) {
		final Map<SimpleName, Type> modifiedVariables = new HashMap<>();
		topLevel.forEach(n -> n.accept(new ReturnVariablesCollector(topLevel), modifiedVariables));
		return modifiedVariables;
	}
	
	public default boolean canBeReturned(List<VariableDeclarationExpr> topLevelVariableDeclarators) {
		return topLevelVariableDeclarators.size() == 1 && topLevelVariableDeclarators.get(0).getVariables().size() == 1;
	}
	
	public default boolean refactorable(Map<SimpleName, Type> usedVariables, List<VariableDeclarationExpr> topLevelVariableDeclarators) {
		return returnsNothing(usedVariables, topLevelVariableDeclarators) || returnsOneThing(usedVariables, topLevelVariableDeclarators);
	}

	public default boolean returnsNothing(Map<SimpleName, Type> usedVariables, List<VariableDeclarationExpr> topLevelVariableDeclarators) {
		return usedVariables.size() == 0 && topLevelVariableDeclarators.size() == 0;
	}
	
	public default boolean returnsOneThing(Map<SimpleName, Type> usedVariables, List<VariableDeclarationExpr> topLevelVariableDeclarators) {
		return (canBeReturned(topLevelVariableDeclarators) && usedVariables.size() == 0) || (!canBeReturned(topLevelVariableDeclarators) && usedVariables.size() == 1);
	}
}
