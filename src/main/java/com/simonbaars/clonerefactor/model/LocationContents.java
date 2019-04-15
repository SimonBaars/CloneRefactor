package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.JavaToken;
import com.github.javaparser.JavaToken.Category;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.simonbaars.clonerefactor.ast.CompareNodes;

public class LocationContents {
	private Range r;
	private final List<Node> nodes;
	private final List<JavaToken> tokens;
	
	private static final Category[] NO_TOKEN = {Category.COMMENT, Category.EOL, Category.WHITESPACE_NO_EOL};
	
	public LocationContents() {
		this.nodes = new ArrayList<>();
		this.tokens = new ArrayList<>();
	}

	public LocationContents(LocationContents contents) {
		this.r = contents.r;
		this.nodes = new ArrayList<>(contents.getNodes());
		this.tokens = new ArrayList<>(contents.getTokens());
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
		//return compareNodes(getNodes(), ((LocationContents)o).getNodes());
		return getTokens().equals(((LocationContents)o).getTokens());
	}
	
	public boolean compareNodes(List<Node> thisNodes, List<Node> otherNodes) {
		if(thisNodes.size()!=otherNodes.size())
			return false;
	
		for(int i = 0; i<thisNodes.size(); i++) {
			if(r.contains(otherNodes.get(i).getRange().get()) && !nodesEqual(thisNodes.get(i), otherNodes.get(i)))
				return false;
			
			if(!compareNodes(thisNodes.get(i).getChildNodes(), otherNodes.get(i).getChildNodes()))
				return false;
		}
		return true;
	}
	
	public boolean nodesEqual(Node n1, Node n2) {
		return n1.equals(n2);
	}
	
	public int getAmountOfTokens() {
		return new Long(tokens.stream().filter(e -> Arrays.stream(NO_TOKEN).noneMatch(c -> c.equals(e.getCategory()))).count()).intValue();
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		for(JavaToken token : tokens) {
			result=prime * result + token.hashCode();
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
		return tokens.stream().map(e -> e.asString()+" "+e.getCategory()).collect(Collectors.joining(","));
	}

	public Range addTokens(Node n, TokenRange tokenRange, Range range) {
		for(JavaToken token : tokenRange) {
			Optional<Range> r = token.getRange();
			if(r.isPresent()) {
				if(!range.contains(r.get())) break;
				tokens.add(token);
				if(n instanceof ClassOrInterfaceDeclaration && token.asString().equals("{")) break; // We cannot exclude the body of class files, this is a workaround.
			}
		}
		r = new Range(tokens.get(0).getRange().get().begin, tokens.get(tokens.size()-1).getRange().get().begin);
		return r;
	}

	public List<JavaToken> getTokens() {
		return tokens;
	}

	public void merge(LocationContents contents) {
		nodes.addAll(contents.getNodes());
		tokens.addAll(contents.getTokens());
	}
	
	
}
