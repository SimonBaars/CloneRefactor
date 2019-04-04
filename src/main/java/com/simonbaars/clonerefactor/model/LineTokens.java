package com.simonbaars.clonerefactor.model;

import java.util.List;

import com.github.javaparser.ast.Node;

public class LineTokens {
	private final List<Node> tokens;

	public LineTokens(List<Node> tokens) {
		super();
		this.tokens = tokens;
	}

	public List<Node> getTokens() {
		return tokens;
	}
	
}
