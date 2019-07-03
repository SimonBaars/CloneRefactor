package com.simonbaars.clonerefactor.metrics.enums;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class CloneRefactorability implements MetricEnum<Refactorability>, RequiresNodeOperations {
	public enum Refactorability{
		CANBEEXTRACTED, //Can be extracted
		NOEXTRACTIONBYCONTENTTYPE, //When the clone is not a partial method
		PARTIALBLOCK, //When the clone spans part of a block (TODO: can we make the clone smaller to not make it a partial block, or should we turn it into a type 3 clone?)
		COMPLEXCONTROLFLOW, //When the clone spans break, continue or return statements. However, exceptions apply:
							// - All flows end in return OR break OR continue
							// - The for loop that is being `continue` or `break` is included
						
	}

	@Override
	public Refactorability get(Sequence sequence) {
		if(new CloneContents().get(sequence)!=CloneContents.ContentsType.PARTIALMETHOD)
			return Refactorability.NOEXTRACTIONBYCONTENTTYPE;
		for(Location location : sequence.getLocations()) {
			for(Node n : location.getContents().getNodes()) {
				List<Node> children = childrenToParse(n);
				if(children.stream().anyMatch(e -> !isExcluded(e) && !location.getContents().getNodes().contains(e)))
					return Refactorability.PARTIALBLOCK;
			}
		}
		if(sequence.getLocations().stream().anyMatch(e -> e.getContents().getNodes().stream().anyMatch(n -> complexControlFlow(n))) && !flowEndsInReturnAndContainsOnlyReturnStatements(sequence))
			return Refactorability.COMPLEXCONTROLFLOW;
		return Refactorability.CANBEEXTRACTED;
	}
	
	private boolean complexControlFlow(Node n) {
		return n instanceof BreakStmt || n instanceof ReturnStmt || n instanceof ContinueStmt;
	}
	
	private boolean flowEndsInReturnAndContainsOnlyReturnStatements(Sequence sequence) {
		Location location = sequence.getAny();
		if(location.getContents().getNodes().stream().filter(n -> complexControlFlow(n)).allMatch(e -> e instanceof ReturnStmt) 
				&& location.getContents().getNodes().get(location.getContents().getNodes().size()-1) instanceof ReturnStmt)
			return true;
		return false;
	}
}
