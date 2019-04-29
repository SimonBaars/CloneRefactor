package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.Node;

public class ComparableToken implements Compare {
	
	private final Node node;
	
	public ComparableToken(Node node) {
		super();
		this.node = node;
	}

	@Override
	public boolean equals(Object o) {
		return node.equals(((ComparableToken)o).node);
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
