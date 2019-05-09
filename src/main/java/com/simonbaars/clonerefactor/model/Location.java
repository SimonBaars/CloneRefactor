package com.simonbaars.clonerefactor.model;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.metrics.enums.CloneLocation;
import com.simonbaars.clonerefactor.metrics.enums.CloneLocation.LocationType;

public class Location implements Comparable<Location> {
	private final Path file;
	private Range range;
	
	private LocationContents contents = new LocationContents();
	
	private Location prevLocation;
	private Location clone;
	private Location nextLocation;

	private LocationType locationType;

	public Location(Path file) {
		this.file=file;
	}

	public Location(Path file, Location prevLocation) {
		this(file);
		this.prevLocation = prevLocation;
	}

	public Location(Location clonedLocation) {
		this.file = clonedLocation.file;
		this.contents = new LocationContents(clonedLocation.contents);
		this.range = new Range(clonedLocation.range.begin, clonedLocation.range.end);
		this.prevLocation = clonedLocation.prevLocation;
		this.clone = clonedLocation.clone;
		this.nextLocation = clonedLocation.nextLocation;
	}

	public Location(Path file, Range range) {
		this.file = file;
		this.range = range;
	}

	public Location(Location clonedLocation, Range r) {
		this(clonedLocation);
		this.range = r;
	}

	public Path getFile() {
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
	
	public int getEffectiveLines() {
		return getContents().getEffectiveLines().size();
	}

	public boolean isSame(Location other) {
		if (range != other.range)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		}
		return file.equals(other.file);
	}

	public int getAmountOfTokens() {
		return getContents().getAmountOfTokens();
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

	public Location mergeWith(Location oldClone) {
		if(file != oldClone.getFile())
			throw new IllegalStateException("Files of merging locations do not match! "+file+" != "+oldClone.getFile());
		Location copy = new Location(this);
		copy.contents.merge(oldClone.getContents());
		copy.range = getRange().withEnd(oldClone.getRange().end);
		return copy;
	}

	public int getAmountOfNodes() {
		return getContents().getNodes().size();
	}

	public LocationType getLocationType() {
		return locationType;
	}
	
	public void setMetrics(CloneLocation l) {
		this.locationType = l.get(this);
	}

	@Override
	public int compareTo(Location o) {
		int stringCompare = file.compareTo(o.file);
		if(stringCompare == 0)
			return range.begin.compareTo(o.range.begin);
		return stringCompare;
	}
}
