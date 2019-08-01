package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.AssignExpr.Operator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.simonbaars.clonerefactor.refactoring.visitor.ReturnVariablesCollector;

public class PopulateReturnValue implements PopulatesExtractedMethod {	
	private NameExpr name;
	private Type type;
	private ReturnStmt retStmt;
	private boolean wipe;
	
	public PopulateReturnValue() {
		reset();
	}

	@Override
	public void prePopulate(MethodDeclaration extractedMethod, List<Node> topLevel) {
		populateIfSingleDeclarator(extractedMethod);
		final List<SimpleName> usedVariables = new ArrayList<>();
		topLevel.forEach(n -> n.accept(new ReturnVariablesCollector(topLevel), usedVariables));
		if(usedVariables.size() == 1) {
			NameExpr name = new NameExpr(usedVariables.get(0));
			createReturn(name, null /*TODO*/, name, false);
		}
			
	}

	private void populateIfSingleDeclarator(MethodDeclaration extractedMethod) {
		NodeList<Statement> statements = extractedMethod.getBody().get().getStatements();
		if(statements.size() == 1 && statements.get(0) instanceof ExpressionStmt) {
			ExpressionStmt exprStmt = (ExpressionStmt)statements.get(0);
			if(exprStmt.getExpression() instanceof VariableDeclarationExpr)
				convertVariableDeclarationToReturn(extractedMethod, exprStmt);
		}
	}

	private void convertVariableDeclarationToReturn(MethodDeclaration extractedMethod, ExpressionStmt exprStmt) {
		NodeList<VariableDeclarator> variables = ((VariableDeclarationExpr)exprStmt.getExpression()).getVariables();
		if(variables.size() == 1) {
			VariableDeclarator variableDeclarator = variables.get(0);
			Optional<Expression> initializer = variableDeclarator.getInitializer();
			if(initializer.isPresent()) {
				createReturn(variableDeclarator.getNameAsExpression(), variableDeclarator.getType(), initializer.get(), true);
			}
		}
	}

	private void createReturn(NameExpr name, Type type, Expression initializer, boolean wipe) {
		this.retStmt = new ReturnStmt(initializer);
		this.wipe = wipe;
		this.name = name;
		this.type = type;
	}

	@Override
	public Optional<Statement> modifyMethodCall(MethodCallExpr expr) {
		if(type!=null && name!=null)
			return Optional.of(new ExpressionStmt(new VariableDeclarationExpr(new VariableDeclarator(type, name.getNameAsString(), expr))));
		else if(name!=null)
			return Optional.of(new ExpressionStmt(new AssignExpr(name, expr, Operator.ASSIGN)));
		return Optional.empty();
	}

	@Override
	public void postPopulate(MethodDeclaration extractedMethod) {
		if(wipe)
			extractedMethod.getBody().get().getStatements().clear();
		if(retStmt !=null)
			extractedMethod.getBody().get().getStatements().add(retStmt);
		reset();
	}

	private void reset() {
		wipe = false;
		retStmt = null;
		name = null;
		type = null;
	}

}
