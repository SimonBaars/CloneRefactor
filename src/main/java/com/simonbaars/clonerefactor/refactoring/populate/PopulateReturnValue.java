package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.simonbaars.clonerefactor.metrics.context.interfaces.ChecksReturningData;

public class PopulateReturnValue implements PopulatesExtractedMethod, ChecksReturningData {	
	private NameExpr name;
	private Type type;
	private ReturnStmt retStmt;
	private boolean wipe;
	
	public PopulateReturnValue() {
		reset();
	}

	@Override
	public void prePopulate(MethodDeclaration extractedMethod, List<Node> topLevel) {
		returnDirectlyIfSingleDeclarator(topLevel);
		if(!collectAllReturners(topLevel))
			collectAllReturningNameExpr(topLevel);
	}

	private boolean collectAllReturners(List<Node> topLevel) {
		List<VariableDeclarationExpr> topLevelVariableDeclarators = getTopLevelDeclarators(topLevel);
		if(canBeReturned(topLevelVariableDeclarators)) {
			VariableDeclarator vd = topLevelVariableDeclarators.get(0).getVariable(0);
			createReturn(vd.getNameAsExpression(), vd.getType(), vd.getNameAsExpression(), false);
			return true;
		}
		return false;
	}

	private void collectAllReturningNameExpr(List<Node> topLevel) {
		final Map<SimpleName, Type> usedVariables = getUsedVariables(topLevel);
		if(usedVariables.size() == 1) {
			Entry<SimpleName, Type> e = usedVariables.entrySet().iterator().next();
			createReturn(new NameExpr(e.getKey()), e.getValue(), new NameExpr(e.getKey()), false);
		}
	}

	private void returnDirectlyIfSingleDeclarator(List<Node> topLevel) {
		if(topLevel.size() == 1 && topLevel.get(0) instanceof ExpressionStmt) {
			ExpressionStmt exprStmt = (ExpressionStmt)topLevel.get(0);
			if(exprStmt.getExpression() instanceof VariableDeclarationExpr)
				convertVariableDeclarationToReturn(exprStmt);
		}
	}

	private void convertVariableDeclarationToReturn(ExpressionStmt exprStmt) {
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
		if(type!=null)
			extractedMethod.setType(type);
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
