package com.simonbaars.clonerefactor.detection.type3;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.detection.model.location.Location;

public class Type3Location extends Location implements Type3Calculation{
	private final List<Location> locations = new ArrayList<>();
	private final List<Location> gaps = new ArrayList<>();

	public Type3Location(Location clonedLocation, Range r) {
		super(clonedLocation, r);
	}

	public Type3Location(Location clonedLocation) {
		super(clonedLocation);
	}

	public Type3Location(Path file, Range range) {
		super(file, range);
	}

	public Type3Location(Location location, Location location2) {
		super(location.getFile(), location.getRange());
		if(location.getRange().isBefore(location2.getRange().begin))
			mergeLocations(location, location2);
		else mergeLocations(location2, location);
	}

	private void mergeLocations(Location before, Location after) {
		if(before instanceof Type3Location) {
			locations.addAll(((Type3Location)before).locations);
			gaps.addAll(((Type3Location)before).gaps);
		} else locations.add(before);
		gaps.add(new Location(before.getFile(), calculateDiffContents(before, after)));
		if(after instanceof Type3Location) {
			locations.addAll(((Type3Location)before).locations);
			gaps.addAll(((Type3Location)after).gaps);
		} else locations.add(after);
		populateContents(getContents(), before.getContents());
		populateContents(getContents(), after.getContents());
		setRange(before.getRange().withEnd(after.getRange().end));
	}

	public List<Location> getLocations() {
		return locations;
	}

	public List<Location> getGaps() {
		return gaps;
	}
	
	@Override
	public Set<Integer> lines(){
		Set<Integer> lines = new HashSet<>();
		for(Location loc : locations) lines.addAll(loc.lines());
		return lines;
	}
}
