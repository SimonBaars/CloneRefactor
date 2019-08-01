package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;

public class PopulateReturnValue implements PopulatesExtractedMethod {
	public PopulateReturnValue() {}

	@Override
	public void prePopulate(MethodDeclaration extractedMethod, List<Node> topLevel) {
		// Does not pre populate
		
	}

	@Override
	public void modifyMethodCall(MethodCallExpr expr) {
		// Does not modify method call
	}

	@Override
	public void postPopulate(MethodDeclaration extractedMethod) {
		NodeList<Statement> statements = extractedMethod.getBody().get().getStatements();
		if(statements.size() == 1 && statements.get(0) instanceof ExpressionStmt) {
			ExpressionStmt exprStmt = (ExpressionStmt)statements.get(0);
			if(exprStmt.getExpression() instanceof VariableDeclarationExpr) {
				NodeList<VariableDeclarator> variables = ((VariableDeclarationExpr)exprStmt.getExpression()).getVariables();
				if(variables.size() == 1) {
					Optional<Expression> initializer = variables.get(0).getInitializer();
					if(initializer.isPresent()) {
						statements.clear();
						statements.add(new ReturnStmt(initializer.get()));
					}
				}
			}
			
		}
	}

}
