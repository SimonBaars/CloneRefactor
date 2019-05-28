package com.simonbaars.clonerefactor.detection.type2;

import com.simonbaars.clonerefactor.ast.interfaces.DeterminesNodeTokens;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class Type2Location implements DeterminesNodeTokens {
	private final int locationIndex;
	private final int statementIndex;
	private final Type2Contents contents;
	private Type2Location next;
	private final Type2Location prev;
	private final Type2Location mergedWith;
	
	public Type2Location(int locationIndex, int statementIndex, Type2Contents contents, Type2Location prev) {
		super();
		this.locationIndex = locationIndex;
		this.statementIndex = statementIndex;
		this.contents = contents;
		this.prev = prev;
		this.mergedWith = null;
	}
	
	public Type2Location(Type2Location type2Statement, Type2Location key) {
		this.locationIndex = type2Statement.locationIndex;
		this.statementIndex = type2Statement.statementIndex;
		this.contents = type2Statement.contents;
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
	
	public Type2Contents getContents() {
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
				+ contents + "]";
	}

	public Type2Location getPrev() {
		return prev;
	}

	public Type2Location mergeWith(Type2Location key) {
		return new Type2Location(this, key);
	}

	public int getAmountOfNodes() {
		if(mergedWith == null)
			return 1;
		return mergedWith.getAmountOfNodes() + 1;
	}
	
	public Location convertToLocation(Sequence sequence) {
		return convertToLocation(sequence.getSequence().get(locationIndex));
	}

	private Location convertToLocation(Location location) {
		return new Location(location.getFile(), location.getContents().getNodes().get(statementIndex));
	}
	
	public Type2Location getLast() {
		if(mergedWith == null)
			return this;
		return mergedWith.getLast();
	}
}
