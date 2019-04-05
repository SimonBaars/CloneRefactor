package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.List;

public class Chain {
	final List<Location> chain;

	public Chain(List<Location> chain) {
		super();
		this.chain = chain;
	}

	public Chain() {
		super();
		this.chain = new ArrayList<>();
	}

	public List<Location> getChain() {
		return chain;
	}
	
}
