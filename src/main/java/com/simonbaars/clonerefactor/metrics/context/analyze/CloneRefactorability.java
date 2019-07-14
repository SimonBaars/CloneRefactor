package com.simonbaars.clonerefactor.metrics.context.analyze;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.metrics.context.interfaces.MetricEnum;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class CloneRefactorability implements MetricEnum<Refactorability>, RequiresNodeOperations {
	public enum Refactorability{
		CANBEEXTRACTED, //Can be extracted
		NOEXTRACTIONBYCONTENTTYPE, //When the clone is not a partial method
		PARTIALBLOCK, //When the clone spans part of a block (TODO: can we make the clone smaller to not make it a partial block, or should we turn it into a type 3 clone?)
		COMPLEXCONTROLFLOW, //When the clone spans break, continue or return statements. However, exceptions apply:
							// - All flows end in return (however not fully implemented)
							// - The for loop that is being `continue` or `break` is included
	}

	@Override
	public Refactorability get(Sequence sequence) {
		if(new CloneContents().get(sequence)!=CloneContents.ContentsType.PARTIALMETHOD)
			return Refactorability.NOEXTRACTIONBYCONTENTTYPE;
		else if(isPartialBlock(sequence))
			return Refactorability.PARTIALBLOCK;
		else if(hasComplexControlFlow(sequence))
			return Refactorability.COMPLEXCONTROLFLOW;
		return Refactorability.CANBEEXTRACTED;
	}
	
	private boolean isPartialBlock(Sequence sequence) {
		for(Location location : sequence.getLocations()) {
			for(Node n : location.getContents().getNodes()) {
				List<Node> children = childrenToParse(n);
				if(children.stream().anyMatch(e -> !isExcluded(e) && !location.getContents().getNodes().contains(e)))
					return true;
			}
		}
		return false;
	}
	
	private boolean hasComplexControlFlow(Sequence sequence) {
		return !loopForAllBreakAndContinueStatementsIsIncluded(sequence.getAny()) || !allPathsReturn(sequence.getAny());
	}
	
	private boolean loopForAllBreakAndContinueStatementsIsIncluded(Location l) {
		List<Node> breakAndContinueStatements = l.getContents().getNodes().stream().filter(n -> continueOrBreak(n)).collect(Collectors.toList());
		for(Node breakOrContinue : breakAndContinueStatements) {
			Optional<SimpleName> label = label(breakOrContinue);
			if(!l.getContents().getNodes().contains(getLoop(breakOrContinue.getParentNode().get(), label, breakOrContinue instanceof ContinueStmt)))
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
	
	public Node getLoop(Node n, Optional<SimpleName> label, boolean isContinue) {
		if((isContinue ? canBeContinued(n) : canBeBroken(n)) && (!label.isPresent() || (n.getParentNode().get() instanceof LabeledStmt && ((LabeledStmt)n.getParentNode().get()).getLabel().equals(label.get())))) {
			return n;
		}
		return getLoop(n.getParentNode().get(), label, isContinue);
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
		return nodeDepth(lastNode) == nodeDepth(l.getContents().getNodes().get(0));
	}
}
