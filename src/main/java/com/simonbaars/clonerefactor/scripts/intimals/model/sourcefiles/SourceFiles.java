package com.simonbaars.clonerefactor.scripts.intimals.model.sourcefiles;

import java.util.HashMap;
import java.util.Map;

public class SourceFiles {
	private Map<String, SourceFile> sourceFiles;

	public SourceFiles() {
		super();
		this.sourceFiles = new HashMap<>();
	}

	public Map<String, SourceFile> getSourceFiles() {
		return sourceFiles;
	}

	public void setSourceFiles(Map<String, SourceFile> sourceFiles) {
		this.sourceFiles = sourceFiles;
	}

	@Override
	public String toString() {
		return "SourceFiles [sourceFiles=" + sourceFiles + "]";
	}
}
