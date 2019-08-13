package com.simonbaars.clonerefactor.metrics.context.analyze;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.Type;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.metrics.context.enums.ContentsType;
import com.simonbaars.clonerefactor.metrics.context.enums.Refactorability;
import com.simonbaars.clonerefactor.metrics.context.interfaces.ChecksReturningData;
import com.simonbaars.clonerefactor.metrics.context.interfaces.DeterminesMetric;

public class CloneRefactorability implements DeterminesMetric<Refactorability>, RequiresNodeOperations, ChecksReturningData {
	@Override
	public Refactorability get(Sequence sequence) {
		List<Node> lowestNodes = lowestNodes(sequence.getAny().getContents().getNodes());
		if(new CloneContents().get(sequence)!=ContentsType.PARTIALMETHOD)
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
			return br.getValue().isPresent() && br.getValue().get() instanceof NameExpr ? 
					Optional.of(((NameExpr)br.getValue().get()).getName()) : Optional.empty();
		}
		return ((ContinueStmt)breakOrContinue).getLabel();
	}
	
	private boolean continueOrBreak(Node n) {
		return n instanceof BreakStmt || n instanceof ContinueStmt;
	}
	
	private<T> boolean allPathsReturn(Location l) {
		List<ReturnStmt> returnStatements = l.getContents().getNodes().stream().filter(n -> n instanceof ReturnStmt).map(n -> (ReturnStmt)n).collect(Collectors.toList());
		if(returnStatements.isEmpty())
			return true;
		Node lastNode = l.getContents().getNodes().get(l.getContents().getNodes().size()-1);
		if(!(lastNode instanceof ReturnStmt))
			return false;
		return nodeDepth(lastNode) == nodeDepth(l.getFirstNode());
	}
}
