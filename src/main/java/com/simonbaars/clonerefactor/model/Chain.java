package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Chain {
	final List<Location> chain;

	public Chain(List<Location> collection) {
		super();
		this.chain = collection;
	}

	public Chain() {
		super();
		this.chain = new ArrayList<>();
	}

	public List<Location> getChain() {
		return chain;
	}
	
	public void add(Location l) {
		chain.add(l);
	}

	public int size() {
		return chain.size();
	}
}
