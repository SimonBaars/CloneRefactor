package com.simonbaars.clonerefactor.detection.model.location;

import java.nio.file.Path;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.context.analyze.CloneLocation;
import com.simonbaars.clonerefactor.context.enums.LocationType;

public class Location implements Comparable<Location>, HasRange {
	private final Path file;
	private Range range;
	public boolean isVisited = false;
	
	private final LocationContents contents;
	
	private Location prev;
	private Location clone;
	private Location next;

	private LocationType locationType;

	public Location(Location clonedLocation) {
		this(clonedLocation, clonedLocation.range);
	}

	public Location(Path file, Range range) {
		this.file = file;
		this.range = range;
		this.contents = new LocationContents(range);
	}

	public Location(Location clonedLocation, Range r) {
		this.file = clonedLocation.file;
		this.contents = new LocationContents(clonedLocation.contents, r);
		this.range = r;
		this.prev = clonedLocation.prev;
		this.clone = clonedLocation.clone;
		this.next = clonedLocation.next;
		this.isVisited = clonedLocation.isVisited;
		if(range!=clonedLocation.range) {
			getContents().stripToRange();
			if(next != null) this.isVisited = next.prev.isRangeVisited(r);
			else if(prev != null) this.isVisited = prev.next.isRangeVisited(r);
		}
	}
	
	private boolean isRangeVisited(Range r) {
		//System.out.println("Determine visited range "+r+" with "+range+"and isvisited "+isVisited);
		if(!r.contains(range))
			return true;
		else if(!isVisited)
			return false;
		else if(next == null)
			return true;
		return next.isRangeVisited(r);
	}

	public Location(Path path, Location prevLocation, Node n) {
		this(path, n);
		this.prev = prevLocation;
	}

	public Location(Path file, Node...nodes) {
		this.file = file;
		this.contents = new LocationContents(nodes);
		this.range = this.contents.getRange();
	}

	public Path getFile() {
		return file;
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
		return getContents().getAmountOfLines();
	}

	public boolean isSame(Location other) {
		if (range != other.range)
			return false;
		return file == null ? other.file == null : file.equals(other.file);
	}

	public int getAmountOfTokens() {
		return getContents().getAmountOfTokens();
	}

	public void setNext(Location nextLocation) {
		this.next = nextLocation;
	}

	public LocationContents getContents() {
		return contents;
	}

	public Range getRange() {
		return range;
	}

	public Location getPrev() {
		return prev;
	}

	public void setPrev(Location prevLocation) {
		this.prev = prevLocation;
	}

	public Location getNext() {
		return next;
	}
	
	public Location setRange(Range r) {
		getContents().setRange(r);
		this.range = r;
		return this;
	}

	public Location mergeWith(Location oldClone) {
		if(file != oldClone.getFile())
			throw new IllegalStateException("Files of merging locations do not match! "+file+" != "+oldClone.getFile());
		Location copy = new Location(this);
		copy.contents.merge(oldClone.getContents());
		copy.syncRanges();
		copy.isVisited = oldClone.isVisited && isVisited;
		return copy;
	}

	private void syncRanges() {
		this.range = contents.getRange();
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

	public String getName() {
		return file.getName(file.getNameCount()-1).toString();
	}

	public boolean overlapsWith(Location other) {
		return range.overlapsWith(other.range);
	}

	public Node getFirstNode() {
		return getContents().getNodes().get(0);
	}

	public Node getLastNode() {
		return getContents().getNodes().get(getContents().getNodes().size()-1);
	}
}
