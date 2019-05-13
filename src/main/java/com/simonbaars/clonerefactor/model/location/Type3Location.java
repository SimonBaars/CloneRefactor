package com.simonbaars.clonerefactor.model.location;

import java.nio.file.Path;

import com.github.javaparser.Range;

public class Type3Location extends Location {
	private final LocationContents diffContents = new LocationContents();

	public Type3Location(Location l2, Range range2, int amountOfNodes, int compareSize) {
		super(l2, range2, amountOfNodes, compareSize);
	}

	public Type3Location(Location clonedLocation, Range r) {
		super(clonedLocation, r);
	}

	public Type3Location(Location clonedLocation) {
		super(clonedLocation);
	}

	public Type3Location(Path file, Location prevLocation) {
		super(file, prevLocation);
	}

	public Type3Location(Path file, Range range) {
		super(file, range);
	}

	public Type3Location(Path file) {
		super(file);
	}

	public Type3Location(Location location, Location location2) {
		super(location.getFile());
		//TODO
	}

	public LocationContents getDiffContents() {
		return diffContents;
	}
}
