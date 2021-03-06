package com.simonbaars.clonerefactor.detection.metrics.interfaces;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BinaryExpr.Operator;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.simonbaars.clonerefactor.detection.model.location.Location;

public interface CalculatesCyclomaticComplexity {
	public default int calculateCC(Node node) {
		return calculateCCIncreace(node) + 1;
	}

	public default int calculateCCIncreace(Node node) {
		List<IfStmt> ifStmts = node.findAll(IfStmt.class);
		List<ForStmt> forStmts = node.findAll(ForStmt.class);
		List<ForEachStmt> foreachStmts = node.findAll(ForEachStmt.class);
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
				foreachStmts.size();
	}

	public default int calculateLocationCCIncrease(Location l) {
		return calculateCCIncrease(l.getContents().getTopLevelNodes());
	}

	public default int calculateCCIncrease(List<Node> nodes) {
		return calculateCCIncrease(nodes.stream());
	}

	public default int calculateCCIncrease(Stream<Node> nodes) {
		return nodes.mapToInt(this::calculateCCIncreace).sum();
	}
}
