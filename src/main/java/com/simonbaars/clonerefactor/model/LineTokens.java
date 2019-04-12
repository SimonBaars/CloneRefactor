package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.ast.CompareNodes;

public class LineTokens {
	private final List<Node> tokens;

	public LineTokens(List<Node> tokens) {
		super();
		this.tokens = tokens;
	}
	
	public LineTokens() {
		this.tokens = new ArrayList<>();
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
	public boolean equals (Object o) {
		if(!(o instanceof LineTokens))
			return false;
		List<Node> compareTokens = ((LineTokens)o).getTokens();
		if(tokens.size()!=compareTokens.size())
			return false;
		CompareNodes c = new CompareNodes();
		//System.out.println("Compare "+toString()+" with "+tokens.toString()+" => "+this.tokens.equals(((LineTokens)compareTokens).getTokens())+" but actually "+(hashCode() == compareTokens.hashCode()));
		return IntStream.range(0,tokens.size()).allMatch(i -> c.compare(tokens.get(i), compareTokens.get(i)));//this.tokens.equals(((LineTokens)compareTokens).getTokens());
	}
	
	@Override
	public int hashCode() {//TODO: This is incorrect
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
