package com.simonbaars.clonerefactor.scripts.intimals.model.sourcefiles;

import java.util.HashMap;
import java.util.Map;

import com.simonbaars.clonerefactor.detection.model.location.Location;

public class SourceFile {
	private Map<Integer, Location> sourceLocations;

	public SourceFile(Map<Integer, Location> sourceLocations) {
		super();
		this.sourceLocations = sourceLocations;
	}

	public SourceFile() {
		this.sourceLocations = new HashMap<>();
	}

	public Map<Integer, Location> getSourceLocations() {
		return sourceLocations;
	}
	
	public Location getLoc(int nodeId) {
		return sourceLocations.get(nodeId);
	}

	public void setSourceLocations(Map<Integer, Location> sourceLocations) {
		this.sourceLocations = sourceLocations;
	}

	@Override
	public String toString() {
		return "SourceFile [sourceLocations=" + sourceLocations + "]";
	}
}
