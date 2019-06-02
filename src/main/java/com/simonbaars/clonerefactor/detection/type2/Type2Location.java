package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.ast.interfaces.DeterminesNodeTokens;
import com.simonbaars.clonerefactor.datatype.IndexRange;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class Type2Location implements DeterminesNodeTokens {
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
	
	public Type2Location(Type2Location type2Statement, Type2Location key) {
		this.locationIndex = type2Statement.locationIndex;
		this.statementIndices = key.statementIndices.withStart(type2Statement.statementIndices.getStart());
		this.contents = new ArrayList<>(type2Statement.contents);
		this.contents.addAll(key.getContents());
		this.prev = type2Statement.prev;
		this.next = type2Statement.next;
		this.mergedWith = key;
	}

	public int getLocationIndex() {
		return locationIndex;
	}
	
	public int getStatementIndex() {
		return statementIndex;
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
		result = prime * result + statementIndex;
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
		return statementIndex == other.statementIndex;
	}

	@Override
	public String toString() {
		return "Type2Statement [locationIndex=" + locationIndex + ", statementIndex=" + statementIndex + ", contents="
				+ contents + ", mergedWith="+mergedWith+"]";
	}

	public Type2Location getPrev() {
		return prev;
	}

	public Type2Location mergeWith(Type2Location key) {
		return new Type2Location(this, key);
	}

	public int size() {
		if(mergedWith == null)
			return 1;
		return mergedWith.size() + 1;
	}
	
	public Location convertToLocation(Sequence sequence) {
		return convertToLocation(sequence.getLocations().get(locationIndex));
	}

	private Location convertToLocation(Location location) {
		return new Location(location.getFile(), getFullInstance(new ArrayList<>()).stream().map(e -> location.getContents().getNodes().get(statementIndex)).toArray(Node[]::new));
	}
	
	public Type2Location getLast() {
		if(mergedWith == null)
			return this;
		return mergedWith.getLast();
	}
	public Type2Location getSecondToLast() {
		if(mergedWith == null)
			throw new IllegalAccessError("This object has no second to last!");
		if(mergedWith.mergedWith == null)
			return this;
		return mergedWith.getSecondToLast();
	}

	public int[][] getFullContents() {
		int[][] fullContents = new int[size()][];
		int i = 0;
		for(Type2Location loc = this;loc!=null; loc=loc.mergedWith)
			fullContents[i++] = loc.contents.getContents();
		return fullContents;
	}

	public Type2Location getMergedWith() {
		return mergedWith;
	}

	public void setMergedWith(Type2Location mergedWith) {
		this.mergedWith = mergedWith;
	}
	
	public List<Type2Location> getFullInstance(List<Type2Location> type2Location) {
		type2Location.add(this);
		if(mergedWith!=null) mergedWith.getFullInstance(type2Location);
		return type2Location;
	}

	public Type2Location splitAt(int amountOfNodes) {
		if(amountOfNodes>1)
			return mergedWith.splitAt(amountOfNodes-1);
		return this;
	}
}
