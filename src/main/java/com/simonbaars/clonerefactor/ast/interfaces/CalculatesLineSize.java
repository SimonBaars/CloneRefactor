package com.simonbaars.clonerefactor.ast.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.detection.model.FiltersTokens;
import com.simonbaars.clonerefactor.detection.model.location.Location;

public interface CalculatesLineSize extends FiltersTokens {
	public default int lineSize(Node node) {
		return filledLines(getEffectiveTokens(node)).size();
	}

	public default Set<Integer> filledLines(Stream<JavaToken> stream) {
		return stream.map(t -> t.getRange()).filter(Optional::isPresent).map(r -> r.get().begin.line).collect(Collectors.toSet());
	}
	
	public default int lineSize(List<JavaToken> tokens) {
		return filledLines(tokens.stream()).size();
	}
	
	public default int lineSize(Location l) {
		return lineSize(l.getContents().getTokens());
	}
}
