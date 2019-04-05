package com.simonbaars.clonerefactor.model;

import java.util.Arrays;
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

	public int size() {
		return tokens.size();
	}
	
	public void add(Node token) {
		tokens.add(token);
	}
	
	@Override
	public boolean equals (Object compareTokens) {
		if(!(compareTokens instanceof LineTokens))
			return false;
		System.out.println("Compare "+toString()+" with "+tokens.toString()+" => "+this.tokens.equals(((LineTokens)compareTokens).getTokens())+" but actually "+(hashCode() == compareTokens.hashCode()));
		return this.tokens.equals(((LineTokens)compareTokens).getTokens());
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		for(Node token : tokens) {
			result=prime * result + token.hashCode();
		}
		return result;
	}

	@Override
	public String toString() {
		return "LineTokens [tokens=" + Arrays.deepToString(tokens.toArray()) + "]";
	}
	
	
}
