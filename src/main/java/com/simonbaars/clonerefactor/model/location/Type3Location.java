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
		if(location.getRange().isBefore(location2.getRange().begin)) {
			mergeLocations(location, location2);
		} else mergeLocations(location2, location);
	}

	private void mergeLocations(Location before, Location after) {
		Range r = before.getRange().withEnd(after.getRange().end);
		populateContents(getContents(), before);
		populateContents(getContents(), after);
		getContents().setRange(r);
		setRange(r);
		calculateDiffContents(before, after);
	}

	private void calculateDiffContents(Location before, Location after) {
		Location line;
		while((line = before.getNextLine()) != null) {
			if(line.getRange().isBefore(after.getRange().begin)) {
				if(line.getRange().isAfter(before.getRange().end)){
					populateContents(diffContents, line);
				}
			} else break;
		}
	}

	private void populateContents(LocationContents contents, Location l) {
		getContents().getNodes().addAll(l.getContents().getNodes());
		getContents().getTokens().addAll(l.getContents().getTokens());
		getContents().getCompare().addAll(l.getContents().getCompare());
	}

	public LocationContents getDiffContents() {
		return diffContents;
	}
}
