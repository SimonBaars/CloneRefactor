package com.simonbaars.clonerefactor.scripts.intimals.model.sourcefiles;

import java.util.Map;

import com.simonbaars.clonerefactor.detection.model.location.Location;

public class SourceFile {
	private Map<Integer, Location> sourceLocations;

	public SourceFile(Map<Integer, Location> sourceLocations) {
		super();
		this.sourceLocations = sourceLocations;
	}

	public Map<Integer, Location> getSourceLocations() {
		return sourceLocations;
	}

	public void setSourceLocations(Map<Integer, Location> sourceLocations) {
		this.sourceLocations = sourceLocations;
	}
}
