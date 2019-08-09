package com.simonbaars.clonerefactor.detection.type2;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.simonbaars.clonerefactor.model.location.Location;

public class Type2RLocation extends Location {
	private final Set<Expression> diffExpressions = new HashSet<>();

	public Type2RLocation(Location clonedLocation, Range r) {
		super(clonedLocation, r);
	}

	public Type2RLocation(Location clonedLocation) {
		super(clonedLocation);
	}

	public Type2RLocation(Path path, Location prevLocation, Node n) {
		super(path, prevLocation, n);
	}

	public Type2RLocation(Path file, Node... nodes) {
		super(file, nodes);
	}

	public Type2RLocation(Path file, Range range) {
		super(file, range);
	}

	public Set<Expression> getDiffExpressions() {
		return diffExpressions;
	}
}
