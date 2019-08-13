package com.simonbaars.clonerefactor.detection.type2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.IndexRange;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.graph.interfaces.DeterminesNodeTokens;

public class Type2Location implements DeterminesNodeTokens, Comparable<Type2Location> {
	private final int locationIndex;
	private final IndexRange statementIndices;
	private final List<Type2Contents> contents;
	private Type2Location next;
	private final Type2Location prev;
	
	public Type2Location(int locationIndex, int statementIndex, Type2Contents contents, Type2Location prev) {
		super();
		this.locationIndex = locationIndex;
		this.statementIndices = new IndexRange(statementIndex);
		this.contents = Collections.singletonList(contents);
		this.prev = prev;
	}
	
	public Type2Location(Type2Location statementLeft, Type2Location statementRight) {
		final Type2Location rightStmt = statementRight;
		if(statementLeft.locationIndex!=statementRight.locationIndex) {
			throw new IllegalStateException("Left and right are in a different location!");
		} else if(Arrays.stream(statementLeft.statementArray()).anyMatch(left -> Arrays.stream(rightStmt.statementArray()).anyMatch(right -> left>=right))) {
			statementRight = statementLeft;
			statementLeft = rightStmt;
		}  
		this.locationIndex = statementLeft.locationIndex;
		this.statementIndices = new IndexRange(statementLeft.statementIndices.getStart(), statementRight.statementIndices.getEnd());
		this.contents = new ArrayList<>(statementLeft.contents);
		this.contents.addAll(statementRight.getContents());
		this.prev = statementLeft.prev;
		this.next = statementLeft.next;
	}

	public Type2Location(Type2Location type2Location, int amountOfNodes) {
		this.locationIndex = type2Location.locationIndex;
		this.statementIndices = type2Location.statementIndices.withEnd(type2Location.statementIndices.getStart()+amountOfNodes-1);
		this.contents = type2Location.getContents().subList(0, amountOfNodes);
		this.next = type2Location.next;
		this.prev = type2Location.prev;
	}

	public int getLocationIndex() {
		return locationIndex;
	}
	
	public IndexRange getStatementIndices() {
		return statementIndices;
	}
	
	public int[] statementArray() {
		return statementIndices.toArray();
	}
	
	public int[] contentArray() {
		return contents.stream().flatMapToInt(e -> IntStream.of(e.getContents())).toArray();
	}
	
	public List<Type2Contents> getContents() {
		return contents;
	}
	
	public Type2Location getNext() {
		return next;
	}
	
	public void setNext(Type2Location next) {
		this.next = next;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + locationIndex;
		result = prime * result + Arrays.hashCode(statementArray());
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
		Type2Location other = (Type2Location) obj;
		if (locationIndex != other.locationIndex)
			return false;
		return Arrays.equals(statementArray(), other.statementArray());
	}

	@Override
	public String toString() {
		return "Type2Statement [locationIndex=" + locationIndex + ", statementIndices=" + statementIndices + ", contents="
				+ Arrays.toString(contents.toArray()) + "]";
	}

	public Type2Location getPrev() {
		return prev;
	}

	public Type2Location mergeWith(Type2Location key) {
		return new Type2Location(this, key);
	}

	public int size() {
		return statementIndices.size();
	}
	
	public Location convertToLocation(Sequence sequence, Type2Sequence seq) {
		return convertToLocation(sequence.getLocations().get(locationIndex), seq);
	}

	private Location convertToLocation(Location location, Type2Sequence seq) {
		return new Location(location.getFile(), statementIndices.stream().boxed().map(i -> location.getContents().getNodes().get(i)).toArray(Node[]::new));
	}
	
	public Type2Location getLast() {
		return getLocationByIndex(statementIndices.getEnd());
	}
	
	private Type2Location getLocationByIndex(int index) {
		if(index == statementIndices.getStart()) {
			return this;
		} else if(index>statementIndices.getStart()) {
			return this.next.getLocationByIndex(index);
		} 
		return this.prev.getLocationByIndex(index);
	}

	public Type2Location getSecondToLast() {
		if(size()<2)
			throw new IllegalAccessError("This object has no second to last!");
		return getLocationByIndex(statementIndices.getEnd()-1);
	}

	public int[][] getFullContents() {
		int[][] fullContents = new int[size()][];
		for(int i = 0; i<contents.size(); i++)
			fullContents[i] = contents.get(i).getContents();	
		return fullContents;
	}

	public Type2Contents getFirstContents() {
		return contents.get(0);
	}

	public int getStatementIndex() {
		return statementIndices.getStart();
	}

	public Type2Location withSize(int amountOfNodes) {
		return new Type2Location(this, amountOfNodes);
	}

	@Override
	public int compareTo(Type2Location o) {
		int res;
		if((res = Integer.compare(locationIndex, o.locationIndex)) != 0)
			return res;
		return Integer.compare(statementIndices.getStart(), o.statementIndices.getStart());
	}
}
