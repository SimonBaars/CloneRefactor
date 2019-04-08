package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sequence {
	final List<Location> chain;

	public Sequence(List<Location> collection) {
		super();
		this.chain = collection;
	}

	public Sequence() {
		super();
		this.chain = new ArrayList<>();
	}

	public List<Location> getChain() {
		return chain;
	}
	
	public Sequence add(Location l) {
		chain.add(l);
		return this; //For method chaining
	}

	public int size() {
		return chain.size();
	}

	@Override
	public String toString() {
		return "Sequence [chain=" + Arrays.toString(chain.toArray()) + "]";
	}
}
