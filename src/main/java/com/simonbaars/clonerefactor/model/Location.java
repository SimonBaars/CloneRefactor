package com.simonbaars.clonerefactor.model;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.ListMap;

public class Location {
	private final File file;
	private int beginLine;
	private int endLine;
	private int amountOfLines = 1;
	private int amountOfTokens = 0;
	private int tokenHash;
	
	private LocationContents tokens = new LocationContents();
	
	private Location prevLine;
	private Location clone;
	private Location nextLine;
	
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

	public Location(File file, int beginLine, int endLine, int amountOfLines, int amountOfTokens) {
		this(file, beginLine, endLine);
		this.amountOfLines = amountOfLines;
		this.amountOfTokens = amountOfTokens;
	}

	public Location(File file, int line, Location prevLocation) {
		this(file, line);
		this.prevLine = prevLocation;
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

	/*@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + beginLine;
		result = prime * result + endLine;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}*/

	public boolean isSame(Location other) {
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

	public Location getNextLine() {
		return nextLine;
	}

	public void setNextLine(Location nextLine) {
		this.nextLine = nextLine;
	}

	public LocationContents getTokens() {
		return tokens;
	}

	public void setTokens(LocationContents tokens) {
		this.tokens = tokens;
		this.amountOfTokens = tokens.size();
	}

	public void incrementTokens() {
		this.amountOfTokens++;
	}

	public void calculateTokens(Node n, int line) {
		Optional<TokenRange> t = n.getTokenRange();
		if(t.isPresent())
			getTokens().addTokens(t.get(), line);
		getTokens().add(n);
		this.amountOfTokens = getTokens().getTokens().size();
	}
	
}
