package com.simonbaars.clonerefactor.ast.interfaces;

import java.util.ListIterator;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;

public interface DeterminesNodeRange extends RequiresNodeOperations {
	public default Range getValidRange(Node n) {
		Range nodeRange = n.getRange().get();
		for(ListIterator<Node> it = n.getChildNodes().listIterator(n.getChildNodes().size()); it.hasPrevious(); ) {
			Node node = it.previous();
			if(!isExcluded(node) && node.getRange().isPresent()) {
				nodeRange = nodeRange.withEnd(node.getRange().get().begin);
			} else break;
		}
		return nodeRange;
	}
}
