package com.simonbaars.clonerefactor.metrics.enums;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.ast.RequiresNodeOperations;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneRefactorability implements MetricEnum<Refactorability>, RequiresNodeOperations {
	public enum Refactorability{
		CANBEEXTRACTED,
		NOEXTRACTIONBYCONTENTTYPE,
		CANNOTBEEXTRACTED
	}

	@Override
	public Refactorability get(Sequence sequence) {
		if(new CloneContents().get(sequence)!=CloneContents.ContentsType.PARTIALMETHOD)
			return Refactorability.NOEXTRACTIONBYCONTENTTYPE;
		for(Location location : sequence.getSequence()) {
			for(Node n : location.getContents().getNodes()) {
				List<Node> children = childrenToParse(n);
				if(children.stream().anyMatch(e -> !isExcluded(e) && !location.getContents().getNodes().contains(e))) {
					return Refactorability.CANNOTBEEXTRACTED;
				}
			}
		}
		return Refactorability.CANBEEXTRACTED;
	}
}
