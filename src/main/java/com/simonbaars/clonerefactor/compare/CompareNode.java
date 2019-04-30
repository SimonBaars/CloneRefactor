package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.Node;

public class CompareNode implements Compare {
	
	private final Node node;
	
	public CompareNode(Node node) {
		super();
		this.node = node;
	}

	@Override
	public boolean equals(Object o) {
		return node.equals(((CompareNode)o).node);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public int getHashCode() {
		return node.hashCode();
	}

	@Override
	public String toString() {
		return "CompareNode [node=" + node + "]";
	}
}
