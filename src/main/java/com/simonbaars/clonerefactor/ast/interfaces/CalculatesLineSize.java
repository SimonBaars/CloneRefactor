package com.simonbaars.clonerefactor.ast.interfaces;

import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.model.FiltersTokens;

public interface CalculatesLineSize extends FiltersTokens {
	public default int lineSize(Node node) {
		return filledLines(node).size();
	}

	public default Set<Integer> filledLines(Node node) {
		return getEffectiveTokens(node).map(t -> t.getRange()).filter(r -> r.isPresent()).map(r -> r.get().begin.line).collect(Collectors.toSet());
	}
}
