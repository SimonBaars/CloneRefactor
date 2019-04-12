package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
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
		return IntStream.range(0,tokens.size()).allMatch(i -> c.compare(tokens.get(i), compareTokens.get(i)));
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		for(Node token : tokens) {
			result=prime * result + getTokenHashCode(token);
		}
		return result;
	}

	private int getTokenHashCode(Node token) {
		if(token instanceof BlockStmt || token instanceof ClassOrInterfaceDeclaration || token instanceof EnumDeclaration)
			return 1; //We need to let `equals` handle this
		return token.hashCode();
	}

	@Override
	public String toString() {
		return "LineTokens [tokens=" + Arrays.deepToString(tokens.toArray()) + "]";
	}
	
	
}
