package com.simonbaars.clonerefactor.scripts.intimals.model.sourcefiles;

import java.nio.file.Paths;

import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.detection.model.location.Location;

public class NodeLocation extends Location {
	private int id;
	
	public NodeLocation(int nodeId, String file, Range range) {
		super(null, Paths.get(file), range);
		this.id = nodeId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
