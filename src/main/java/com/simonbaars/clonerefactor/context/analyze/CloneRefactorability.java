package com.simonbaars.clonerefactor.context.analyze;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.Type;
import com.simonbaars.clonerefactor.context.enums.ContentsType;
import com.simonbaars.clonerefactor.context.enums.Refactorability;
import com.simonbaars.clonerefactor.context.interfaces.ChecksReturningData;
import com.simonbaars.clonerefactor.context.interfaces.DeterminesMetric;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.graph.interfaces.RequiresNodeOperations;

public class CloneRefactorability implements DeterminesMetric<Refactorability>, RequiresNodeOperations, ChecksReturningData {
	@Override
	public Refactorability get(Sequence sequence) {
		List<Node> lowestNodes = lowestNodes(sequence.getAny().getContents().getNodes());
		if(sequence.getLocations().stream().anyMatch(e -> e.getContents().getContentsType() != ContentsType.PARTIALMETHOD))
			return Refactorability.NOEXTRACTIONBYCONTENTTYPE;
		else if (hasOverlap(sequence))
			return Refactorability.OVERLAPS;
		else if (!lowestNodesAllStatements(lowestNodes))
			return Refactorability.NOSTATEMENT;
		else if(hasMultipleReturn(lowestNodes))
			return Refactorability.MULTIPLERETURNVALUES;
		else if(isPartialBlock(sequence, lowestNodes))
			return Refactorability.PARTIALBLOCK;
		else if(hasComplexControlFlow(sequence))
			return Refactorability.COMPLEXCONTROLFLOW;
		else if(notInClassOrInterface(sequence))
			return Refactorability.NOTINCLASSORINTERFACE;
		return Refactorability.CANBEEXTRACTED;
	}
	
	private boolean notInClassOrInterface(Sequence sequence) {
		return sequence.getLocations().stream().anyMatch(e -> !getClass(e.getFirstNode()).isPresent());
	}

	private boolean hasMultipleReturn(List<Node> lowestNodes) {
		final Map<SimpleName, Type> usedVariables = getUsedVariables(lowestNodes);
		List<VariableDeclarationExpr> topLevelVariableDeclarators = getTopLevelDeclarators(lowestNodes);
		return !refactorable(usedVariables, topLevelVariableDeclarators);
	}

	private boolean lowestNodesAllStatements(List<Node> lowestNodes) {
		return lowestNodes.stream().allMatch(e -> e instanceof Statement);
	}

	private boolean hasOverlap(Sequence sequence) {
		for(int i = 0; i<sequence.size(); i++) {
			for(int j = i+1; j<sequence.size(); j++) {
				Location location1 = sequence.getLocations().get(i);
				Location location2 = sequence.getLocations().get(j);
				if(location1.getFile().equals(location2.getFile()) &&
						location1.overlapsWith(location2))
					return true;
			}
		}
		return false;
	}

	private boolean isPartialBlock(Sequence sequence, List<Node> lowestNodes) {
		Optional<Range> finalEndRange = getFinalEndNode(lowestNodes.get(lowestNodes.size()-1)).getRange();
		if(!lowestNodes.get(0).getRange().isPresent() || !finalEndRange.isPresent())
			return true;
		return !new Range(lowestNodes.get(0).getRange().get().begin, finalEndRange.get().end).equals(sequence.getAny().getRange());
	}
	
	private Node getFinalEndNode(Node node) {
		if(!node.getChildNodes().isEmpty()) {
			Node finalChild = node.getChildNodes().get(node.getChildNodes().size()-1);
			if(!isExcluded(finalChild))
				return getFinalEndNode(finalChild);
		}
		return node;
	}

	private boolean hasComplexControlFlow(Sequence sequence) {
		return !loopForAllBreakAndContinueStatementsIsIncluded(sequence.getAny()) || !allPathsReturn(sequence.getAny());
	}
	
	private boolean loopForAllBreakAndContinueStatementsIsIncluded(Location l) {
		List<Node> breakAndContinueStatements = l.getContents().getNodes().stream().filter(n -> continueOrBreak(n)).collect(Collectors.toList());
		for(Node breakOrContinue : breakAndContinueStatements) {
			Optional<SimpleName> label = label(breakOrContinue);
			Optional<Node> loop = getLoop(breakOrContinue.getParentNode().get(), label, breakOrContinue instanceof ContinueStmt);
			if(loop.isPresent() && !l.getContents().getNodes().contains(loop.get()))
				return false;
		}
		return true;
	}
	
	public boolean canBeContinued(Node n) {
		return n instanceof ForEachStmt || n instanceof ForStmt || n instanceof WhileStmt || n instanceof DoStmt;
	}
	
	public boolean canBeBroken(Node n) {
		return canBeContinued(n) || n instanceof SwitchStmt;
	}
	
	public Optional<Node> getLoop(Node n, Optional<SimpleName> label, boolean isContinue) {
		if((isContinue ? canBeContinued(n) : canBeBroken(n)) && (!label.isPresent() || (n.getParentNode().get() instanceof LabeledStmt && ((LabeledStmt)n.getParentNode().get()).getLabel().equals(label.get())))) {
			return Optional.of(n);
		}
		if(n.getParentNode().isPresent())
			return getLoop(n.getParentNode().get(), label, isContinue);
		return Optional.empty();
	}
	
	private Optional<SimpleName> label(Node breakOrContinue) {
		assert continueOrBreak(breakOrContinue);
		if(breakOrContinue instanceof BreakStmt) {
			BreakStmt br = (BreakStmt)breakOrContinue;
			return br.getLabel();
		}
		return ((ContinueStmt)breakOrContinue).getLabel();
	}
	
	private boolean continueOrBreak(Node n) {
		return n instanceof BreakStmt || n instanceof ContinueStmt;
	}
	
	/**
	 * Check if all control-flow paths in a location return.
	 * This is used to determine if clones with return statements have complex control flow.
	 * If there are no returns, we allow extraction (vacuous truth).
	 * If there are returns, all paths must return to allow extraction.
	 */
	private<T> boolean allPathsReturn(Location l) {
		List<ReturnStmt> returnStatements = l.getContents().getNodes().stream()
				.filter(n -> n instanceof ReturnStmt)
				.map(n -> (ReturnStmt)n)
				.collect(Collectors.toList());
		
		// No returns means no complex control flow from returns
		if(returnStatements.isEmpty())
			return true;
		
		List<Node> nodes = l.getContents().getNodes();
		if(nodes.isEmpty())
			return true;
		
		// Check if all paths through the last statement return
		Node lastNode = nodes.get(nodes.size() - 1);
		return statementReturnsOnAllPaths(lastNode, l.getFirstNode());
	}
	
	/**
	 * Recursively check if a statement returns on all control-flow paths.
	 */
	private boolean statementReturnsOnAllPaths(Node stmt, Node topLevelNode) {
		// Direct return at correct depth
		if(stmt instanceof ReturnStmt) {
			return nodeDepth(stmt) == nodeDepth(topLevelNode);
		}
		
		// If-else: both branches must return
		if(stmt instanceof IfStmt) {
			IfStmt ifStmt = (IfStmt) stmt;
			Statement thenStmt = ifStmt.getThenStmt();
			boolean thenReturns = statementReturnsOnAllPaths(thenStmt, topLevelNode);
			
			if(ifStmt.getElseStmt().isPresent()) {
				boolean elseReturns = statementReturnsOnAllPaths(ifStmt.getElseStmt().get(), topLevelNode);
				return thenReturns && elseReturns;
			} else {
				// No else branch means one path doesn't return
				return false;
			}
		}
		
		// Block: check last statement
		if(stmt instanceof BlockStmt) {
			BlockStmt block = (BlockStmt) stmt;
			if(block.getStatements().isEmpty())
				return false;
			Statement lastStmt = block.getStatements().get(block.getStatements().size() - 1);
			return statementReturnsOnAllPaths(lastStmt, topLevelNode);
		}
		
		// Try-catch: all reachable paths must return
		if(stmt instanceof TryStmt) {
			TryStmt tryStmt = (TryStmt) stmt;
			boolean tryReturns = statementReturnsOnAllPaths(tryStmt.getTryBlock(), topLevelNode);
			
			// All catch clauses must return
			boolean allCatchesReturn = tryStmt.getCatchClauses().isEmpty() ||
					tryStmt.getCatchClauses().stream()
							.allMatch(cc -> statementReturnsOnAllPaths(cc.getBody(), topLevelNode));
			
			// Finally can override return, but that's complex - if present and returns, use that
			if(tryStmt.getFinallyBlock().isPresent()) {
				boolean finallyReturns = statementReturnsOnAllPaths(tryStmt.getFinallyBlock().get(), topLevelNode);
				if(finallyReturns)
					return true; // Finally's return overrides all
			}
			
			return tryReturns && allCatchesReturn;
		}
		
		// Switch: all cases including default must return
		if(stmt instanceof SwitchStmt) {
			SwitchStmt switchStmt = (SwitchStmt) stmt;
			if(switchStmt.getEntries().isEmpty())
				return false;
			
			// Must have default case
			boolean hasDefault = switchStmt.getEntries().stream()
					.anyMatch(entry -> entry.getLabels().isEmpty());
			if(!hasDefault)
				return false;
			
			// All entries must end with return
			return switchStmt.getEntries().stream().allMatch(entry -> {
				if(entry.getStatements().isEmpty())
					return false;
				Statement lastStmt = entry.getStatements().get(entry.getStatements().size() - 1);
				return lastStmt instanceof ReturnStmt || statementReturnsOnAllPaths(lastStmt, topLevelNode);
			});
		}
		
		// Other statements don't guarantee all paths return
		return false;
	}
}
