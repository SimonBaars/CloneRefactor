package com.simonbaars.clonerefactor.model;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.ListMap;

public class Location {
	private final File file;
	private Range range;
	
	private int tokenHash;
	
	private LocationContents contents = new LocationContents();
	
	private Location prevLocation;
	private Location clone;
	private Location nextLocation;

	public Location(File file) {
		this.file=file;
	}

	public Location(File file, Location prevLocation) {
		this(file);
		this.prevLocation = prevLocation;
	}

	public File getFile() {
		return file;
	}
	
	public Location getPrevLine() {
		return prevLocation;
	}

	public void setPrevLine(Location nextLine) {
		this.prevLocation = nextLine;
	}

	public Location getClone() {
		return clone;
	}

	public void setClone(Location clone) {
		this.clone = clone;
	}

	@Override
	public String toString() {
		return "Location [file=" + file + ", range=" + range + "]";
	}
	
	public int getAmountOfLines() {
		return range.end.line-range.begin.line+1;
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
		if (range != other.range)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}

	public int getAmountOfTokens() {
		return getContents().getTokens().size();
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
		return nextLocation;
	}

	public void setNextLine(Location nextLine) {
		this.nextLocation = nextLine;
	}

	public LocationContents getContents() {
		return contents;
	}

	public void setTokens(LocationContents tokens) {
		this.contents = tokens;
	}

	public void calculateTokens(Node n, Range maxRange) {
		Optional<TokenRange> t = n.getTokenRange();
		if(t.isPresent())
			setRange(getContents().addTokens(n, t.get(), maxRange));
		else setRange(maxRange);
		getContents().add(n);
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public Location getPrevLocation() {
		return prevLocation;
	}

	public void setPrevLocation(Location prevLocation) {
		this.prevLocation = prevLocation;
	}

	public Location getNextLocation() {
		return nextLocation;
	}

	public void setNextLocation(Location nextLocation) {
		this.nextLocation = nextLocation;
	}

	public void setContents(LocationContents contents) {
		this.contents = contents;
	}
	
}
