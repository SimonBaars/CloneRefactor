package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.Node;

public class CompareToken implements Compare {
	
	private final Node node;
	
	public CompareToken(Node node) {
		super();
		this.node = node;
	}

	@Override
	public boolean equals(Object o) {
		return node.equals(((CompareToken)o).node);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public int getHashCode() {
		return node.hashCode();
	}
}
