package com.simonbaars.clonerefactor.model;

import java.io.File;
import java.util.List;

import com.simonbaars.clonerefactor.datatype.ListMap;

public class Location {
	private final File file;
	private int beginLine;
	private int endLine;
	private int amountOfLines = 1;
	private int amountOfTokens;
	private int tokenHash;
	
	private Location prevLine;
	private Location clone;
	
	public Location(File file, int line) {
		super();
		this.file = file;
		this.beginLine = line;
		this.endLine = line;
	}

	public Location(File file, int line, int amountOfTokens, int tokenHash) {
		this(file, line);
		this.amountOfTokens = amountOfTokens;
		this.tokenHash = tokenHash;
	}

	public Location(File file, int beginLine, int endLine) {
		this.file = file;
		this.beginLine = beginLine;
		this.endLine = endLine;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + beginLine;
		result = prime * result + endLine;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
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
		Location other = (Location) obj;
		if (beginLine != other.beginLine)
			return false;
		if (endLine != other.endLine)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}

	public int getAmountOfTokens() {
		return amountOfTokens;
	}

	public void setAmountOfTokens(int amountOfTokens) {
		this.amountOfTokens = amountOfTokens;
	}

	public int getTokenHash() {
		return tokenHash;
	}

	public void setTokenHash(int tokenHash) {
		this.tokenHash = tokenHash;
	}

	public boolean isLocationParsed(ListMap<Integer, Location> reg) {
		List<Location> list = reg.get(tokenHash);
		return list.get(list.size()-1) != this;
	}
	
}
