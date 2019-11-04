package com.simonbaars.clonerefactor.scripts.intimals.model.matches;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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

	@Override
	public String toString() {
		return "Match [file=" + file + ", nodes=" + Arrays.toString(nodes.toArray()) + "]";
	}
	
	public String getXMLFile() {
		return new File(file).getName().replace(".java", ".xml");
	}

	public Path getFilePath(String clusterLoc) {
		return Paths.get(file);
	}
}
