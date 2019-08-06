package com.simonbaars.clonerefactor.detection.type2;

import java.nio.file.Path;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.model.location.Location;

public class Type2Location extends Location {

	public Type2Location(Location clonedLocation, Range r) {
		super(clonedLocation, r);
	}

	public Type2Location(Location clonedLocation) {
		super(clonedLocation);
	}

	public Type2Location(Path path, Location prevLocation, Node n) {
		super(path, prevLocation, n);
	}

	public Type2Location(Path file, Node... nodes) {
		super(file, nodes);
	}

	public Type2Location(Path file, Range range) {
		super(file, range);
	}
	
}
