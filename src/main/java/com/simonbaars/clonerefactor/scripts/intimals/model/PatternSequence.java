package com.simonbaars.clonerefactor.scripts.intimals.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.simonbaars.clonerefactor.detection.interfaces.HasSize;

public class PatternSequence implements HasSize {
	private final List<PatternLocation> locations;

	public PatternSequence(List<PatternLocation> collection) {
		super();
		this.locations = collection;
	}

	public PatternSequence() {
		super();
		this.locations = new ArrayList<>();
	}

	public PatternSequence(PatternSequence copy, int begin, int end) {
		this.locations = copy.locations.subList(begin, end);
	}

	public List<PatternLocation> getLocations() {
		return locations;
	}
	
	public PatternSequence add(PatternLocation l) {
		locations.add(l);
		return this;
	}

	public int size() {
		return locations.size();
	}

	@Override
	public String toString() {
		return "Sequence [sequence=" + Arrays.toString(locations.toArray()) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locations == null) ? 0 : locations.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatternSequence other = (PatternSequence) obj;
		return locations == null ? other.locations == null : locations.equals(other.locations);
	}
	
	public int getCountedLineSize() {
		return locations.stream().mapToInt(e -> e.lines().size()).sum();
	}
}
