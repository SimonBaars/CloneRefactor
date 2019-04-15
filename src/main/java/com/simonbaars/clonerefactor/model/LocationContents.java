package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.simonbaars.clonerefactor.ast.CompareNodes;

public class LocationContents {
	private final List<Node> nodes;
	private final List<JavaToken> tokens;
	
	public LocationContents() {
		this.nodes = new ArrayList<>();
		this.tokens = new ArrayList<>();
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public int size() {
		return nodes.size();
	}
	
	public void add(Node n) {
		nodes.add(n);
	}
	
	@Override
	public boolean equals (Object o) {
		if(!(o instanceof LocationContents))
			return false;
		List<Node> compareTokens = ((LocationContents)o).getNodes();
		if(nodes.size()!=compareTokens.size())
			return false;
		CompareNodes c = new CompareNodes();
		return IntStream.range(0,nodes.size()).allMatch(i -> c.compare(nodes.get(i), compareTokens.get(i)));
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		for(Node token : nodes) {
			result=prime * result + getTokenHashCode(token);
		}
		return result;
	}

	private int getTokenHashCode(Node token) {
		if(token instanceof BlockStmt || token instanceof ClassOrInterfaceDeclaration || token instanceof EnumDeclaration)
			return 1; //We need to let `equals` handle this (a hashtable calls `equals` only if the hashcodes are equal)
		return token.hashCode();
	}

	@Override
	public String toString() {
		return "LocationContents [nodes=" + Arrays.deepToString(nodes.toArray()) + "]";
	}

	public Range addTokens(Node n, TokenRange tokenRange, Range range) {
		for(JavaToken token : tokenRange) {
			Optional<Range> r = token.getRange();
			if(r.isPresent()) {
				if(!inRange(r.get(), range)) break;
				tokens.add(token);
			}
		}
		return new Range(tokens.get(0).getRange().get().begin, tokens.get(tokens.size()-1).getRange().get().begin);
	}

	private boolean inRange(Range range, Range inRange) {
		return range.begin.line>=inRange.begin.line &&
				(range.begin.line>inRange.begin.line || range.begin.column >= inRange.begin.column) &&
				range.end.line<=inRange.end.line && 
				(range.end.line<inRange.end.line || range.end.column <= inRange.end.column);
	}

	public List<JavaToken> getTokens() {
		return tokens;
	}
	
	
}
