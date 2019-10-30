package com.simonbaars.clonerefactor.scripts.intimals.model;

import java.util.List;

public class Match {
	private String file;
	private List<Node> nodes;
	
	public Match(String file, List<Node> nodes) {
		super();
		this.file = file;
		this.nodes = nodes;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}
}