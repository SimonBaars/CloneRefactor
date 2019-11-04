package com.simonbaars.clonerefactor.scripts.intimals.model;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.detection.model.location.Location;

public class PatternLocation extends Location {
	
	List<Location> patternComponents;

	public PatternLocation(Location clonedLocation) {
		super(clonedLocation);
	}

	public PatternLocation(Path file, Range range) {
		super(file, range);
	}

	public PatternLocation(Location clonedLocation, Range r) {
		super(clonedLocation, r);
	}

	public PatternLocation(Path path, Location prevLocation, Node n) {
		super(path, prevLocation, n);
	}

	public PatternLocation(Path file, Node... nodes) {
		super(file, nodes);
	}

	public void setComponents(List<Location> locations) {
		this.patternComponents = locations;
		Collections.sort(this.patternComponents);
	}
	
	public Range actualRange() {
		return new Range(this.patternComponents.get(0).getRange().begin, this.patternComponents.get(patternComponents.size()-1).getRange().end);
	}
}
