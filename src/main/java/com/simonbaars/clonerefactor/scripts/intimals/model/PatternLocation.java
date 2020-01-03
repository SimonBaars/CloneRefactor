package com.simonbaars.clonerefactor.scripts.intimals.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.detection.model.location.Location;

public class PatternLocation extends Location {
	
	private List<Location> patternComponents;
	private Range actualRange;

	public PatternLocation(Location clonedLocation) {
		super(clonedLocation);
	}

	public PatternLocation(Location clonedLocation, Range r) {
		super(clonedLocation, r);
	}

	public void setComponents(List<Location> locations) {
		this.patternComponents = locations;
		actualRange = new Range(patternComponents.stream().map(e -> e.getRange().begin).reduce((p1, p2) -> p1.isBefore(p2) ? p1 : p2).get(), patternComponents.stream().map(e -> e.getRange().end).reduce((p1, p2) -> p1.isAfter(p2) ? p1 : p2).get());
	}
	
	public Range actualRange() {
		return actualRange;
	}
	
	@Override
	public Set<Integer> lines(){
		Set<Integer> lines = new HashSet<>();
		for(Location loc : patternComponents) lines.addAll(loc.lines());
		return lines;
	}
}
