package com.simonbaars.clonerefactor.model;

import java.io.File;

public class Location {
	private final File file;
	private int beginLine;
	private int endLine;
	private int amountOfLines = 1;
	
	private Location prevLine;
	private Location clone;
	
	public Location(File file, int line) {
		super();
		this.file = file;
		this.beginLine = line;
		this.endLine = line;
	}

	public Location(File file, int beginLine, int endLine, int amountOfLines) {
		this.file = file;
		this.beginLine = beginLine;
		this.endLine = endLine;
		this.amountOfLines = amountOfLines;
	}

	public File getFile() {
		return file;
	}

	public int getLine() {
		return beginLine;
	}

	public int getBeginLine() {
		return beginLine;
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
		return "Location [file=" + file + ", beginLine=" + beginLine + ", endLine = " + endLine + "]";
	}
	
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	
	public int getEndLine() {
		return endLine;
	}
	
	public int lines() {
		return getEndLine() - getBeginLine() + 1;
	}

	public void setBeginLine(int beginLine) {
		this.beginLine = beginLine;
	}

	public int getAmountOfLines() {
		return amountOfLines;
	}

	public void setAmountOfLines(int amountOfLines) {
		this.amountOfLines = amountOfLines;
	}
	
}
