package com.simonbaars.clonerefactor.scripts.intimals.model;

public class Node {
	private int id;
	private boolean isRoot;
	
	public Node(int id, boolean isRoot) {
		super();
		this.id = id;
		this.isRoot = isRoot;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}
}
