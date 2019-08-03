package com.simonbaars.clonerefactor.metrics.calculators;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.WhileStmt;

public interface CalculatesCyclomaticComplexity {
	public default int calculateCyclomaticComplexity(Node node) {
		List<IfStmt> ifStmts = node.findAll(IfStmt.class);
		List<ForStmt> forStmts = node.findAll(ForStmt.class);
		List<WhileStmt> whileStmts = node.findAll(WhileStmt.class);
		List<DoStmt> doStmts = node.findAll(DoStmt.class);
		List<SwitchEntry> catchStmts = node.findAll(SwitchEntry.class).stream().
				filter(s -> !s.getLabels().isEmpty()) //Don't include "default" statements, only labeled case statements
				.collect(Collectors.toList());
		List<ConditionalExpr> ternaryExprs = node.findAll(ConditionalExpr.class);
		List<BinaryExpr> andExprs = node.findAll(BinaryExpr.class).stream().
				filter(f -> f.getOperator() == Operator.AND).collect(Collectors.toList());
		List<BinaryExpr> orExprs = node.findAll(BinaryExpr.class).stream().
				filter(f -> f.getOperator() == Operator.OR).collect(Collectors.toList());

		return ifStmts.size() +
				forStmts.size() +
				whileStmts.size() +
				doStmts.size() +
				catchStmts.size() +
				ternaryExprs.size() +
				andExprs.size() +
				orExprs.size() +
				1;
	}

	public default int calculateCyclomaticComplexity(Node...nodes) {
		return calculateCyclomaticComplexity(Arrays.asList(nodes));
	}

	public default int calculateCyclomaticComplexity(List<Node> nodes) {
		int total = 1;
		for(Node node : nodes) {
			List<IfStmt> ifStmts = node.findAll(IfStmt.class);
			List<ForStmt> forStmts = node.findAll(ForStmt.class);
			List<WhileStmt> whileStmts = node.findAll(WhileStmt.class);
			List<DoStmt> doStmts = node.findAll(DoStmt.class);
			List<SwitchEntry> catchStmts = node.findAll(SwitchEntry.class).stream().
					filter(s -> !s.getLabels().isEmpty()) //Don't include "default" statements, only labeled case statements
					.collect(Collectors.toList());
			List<ConditionalExpr> ternaryExprs = node.findAll(ConditionalExpr.class);
			List<BinaryExpr> andExprs = node.findAll(BinaryExpr.class).stream().
					filter(f -> f.getOperator() == Operator.AND).collect(Collectors.toList());
			List<BinaryExpr> orExprs = node.findAll(BinaryExpr.class).stream().
					filter(f -> f.getOperator() == Operator.OR).collect(Collectors.toList());

			total+= ifStmts.size() +
					forStmts.size() +
					whileStmts.size() +
					doStmts.size() +
					catchStmts.size() +
					ternaryExprs.size() +
					andExprs.size() +
					orExprs.size();
		}
		return total;
	}
}
