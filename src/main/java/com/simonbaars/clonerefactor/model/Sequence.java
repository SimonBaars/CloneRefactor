package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sequence {
	final List<Location> sequence;

	public Sequence(List<Location> collection) {
		super();
		this.sequence = collection;
	}

	public Sequence() {
		super();
		this.sequence = new ArrayList<>();
	}

	public List<Location> getSequence() {
		return sequence;
	}
	
	public Sequence add(Location l) {
		sequence.add(l);
		return this; //For method chaining
	}

	public int size() {
		return sequence.size();
	}

	@Override
	public String toString() {
		return "Sequence [sequence=" + Arrays.toString(sequence.toArray()) + "]";
	}
}
