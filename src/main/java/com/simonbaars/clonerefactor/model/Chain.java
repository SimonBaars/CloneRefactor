package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Chain {
	final Collection<Location> chain;

	public Chain(Collection<Location> collection) {
		super();
		this.chain = collection;
	}

	public Chain() {
		super();
		this.chain = new ArrayList<>();
	}

	public Collection<Location> getChain() {
		return chain;
	}
	
	public void add(Location l) {
		chain.add(l);
	}

	public int size() {
		return chain.size();
	}
}
