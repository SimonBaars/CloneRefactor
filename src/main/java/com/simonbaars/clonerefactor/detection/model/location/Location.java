package com.simonbaars.clonerefactor.detection.model.location;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.context.analyze.CloneLocation;
import com.simonbaars.clonerefactor.context.enums.LocationType;
import com.simonbaars.clonerefactor.settings.Settings;

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
	
	public Location(Settings s, Path file, Range r) {
		this.file = file;
		this.range = r;
		this.contents = new LocationContents(s, r);
	}

	public Location(Path file, LocationContents contents) {
		this.file = file;
		this.range = contents.getRange();
		this.contents = contents;
	}

	public Location(Location clonedLocation, Range r) {
		this.file = clonedLocation.file;
		this.contents = new LocationContents(clonedLocation.contents.settings, clonedLocation.contents, r);
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
		if(!r.contains(range))
			return true;
		else if(!isVisited)
			return false;
		else if(next == null)
			return true;
		return next.isRangeVisited(r);
	}

	public Location(Settings s, Path file, Node...nodes) {
		this.file = file;
		this.contents = new LocationContents(s, nodes);
		this.range = this.contents.getRange();
	}

	public Location(Settings settings, Path path, Location prevLocation, Node n) {
		this(settings, path, n);
		this.prev = prevLocation;
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
	
	public int getNumberOfLines() {
		return getContents().getAmountOfLines();
	}

	public boolean isSame(Location other) {
		if (range != other.range)
			return false;
		return file == null ? other.file == null : file.equals(other.file);
	}

	public int getNumberOfTokens() {
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

	public int getNumberOfNodes() {
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
	
	public Set<Integer> lines(){
		return IntStream.rangeClosed(getRange().begin.line, getRange().end.line).boxed().collect(Collectors.toSet());
	}
}
