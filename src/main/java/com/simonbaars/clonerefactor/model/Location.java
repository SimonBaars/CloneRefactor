package com.simonbaars.clonerefactor.model;

import java.io.File;

public class Location {
	private final File file;
	private final int line;
	
	public Location(File file, int line) {
		super();
		this.file = file;
		this.line = line;
	}

	public File getFile() {
		return file;
	}

	public int getLine() {
		return line;
	}

	@Override
	public String toString() {
		return "Location [file=" + file + ", line=" + line + "]";
	}
	
}
