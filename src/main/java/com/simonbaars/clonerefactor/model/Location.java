package com.simonbaars.clonerefactor.model;

import java.io.File;

public class Location {
	private final File file;
	private final int line;
	
	private Location prevLine;
	private Location clone;
	
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

	public Location getPrevLine() {
		return prevLine;
	}

	public void setPrevLine(Location nextLine) {
		this.prevLine = nextLine;
	}

	public Location getClone() {
		return clone;
	}

	public void setClone(Location clone) {
		this.clone = clone;
	}

	@Override
	public String toString() {
		return "Location [file=" + file + ", line=" + line + "]";
	}
	
}
