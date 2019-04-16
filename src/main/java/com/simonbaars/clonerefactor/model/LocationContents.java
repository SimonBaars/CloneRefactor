package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.JavaToken;
import com.github.javaparser.JavaToken.Category;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.simonbaars.clonerefactor.exception.NoTokensException;

public class LocationContents {
	private Range range;
	private final List<Node> nodes;
	private final List<JavaToken> tokens;
	
	private static final Category[] NO_TOKEN = {Category.COMMENT, Category.EOL, Category.WHITESPACE_NO_EOL};
	
	public LocationContents() {
		this.nodes = new ArrayList<>();
		this.tokens = new ArrayList<>();
	}

	public LocationContents(LocationContents contents) {
		this.range = contents.range;
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
		return compareNodes(getNodes(), ((LocationContents)o).getNodes());
	}
	
	public boolean compareNodes(List<Node> thisNodes, List<Node> otherNodes) {
		if(thisNodes.size()!=otherNodes.size())
			return false;
		
		for(int i = 0; i<thisNodes.size(); i++) {
			Optional<Range> rangeOptional = thisNodes.get(i).getRange();
			if(rangeOptional.isPresent() && range.contains(rangeOptional.get())){
				if(!nodesEqual(thisNodes.get(i), otherNodes.get(i)))
					return false;
			} else if(rangeOptional.isPresent() && rangeOptional.get().contains(range) && !compareNodes(thisNodes.get(i).getChildNodes(), otherNodes.get(i).getChildNodes()))
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
	
	public int calcHashcode(List<Node> nodeList, int result, int prime) {
		for(Node node : nodeList) {
			Optional<Range> rangeOptional = node.getRange();
			if(rangeOptional.isPresent() && range.contains(rangeOptional.get()))
				result = prime*result*node.hashCode();
			result = calcHashcode(node.getChildNodes(), result, prime);
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = calcHashcode(nodes, prime, result);
		return result;
	}

	@Override
	public String toString() {
		return tokens.stream().map(e -> e.asString()).collect(Collectors.joining());
	}

	public Range addTokens(Node n, TokenRange tokenRange, Range validRange) {
		for(JavaToken token : tokenRange) {
			Optional<Range> r = token.getRange();
			if(r.isPresent()) {
				if(!validRange.contains(r.get())) break;
				tokens.add(token);
				if((n instanceof ClassOrInterfaceDeclaration || n instanceof EnumDeclaration) && token.asString().equals("{")) break; // We cannot exclude the body of class files, this is a workaround.
			}
		}
		if(tokens.isEmpty())
			throw new NoTokensException(n, tokenRange, validRange);
		range = new Range(tokens.get(0).getRange().get().begin, tokens.get(tokens.size()-1).getRange().get().end);
		return range; //No, we can't merge this with the line above.
	}

	public List<JavaToken> getTokens() {
		return tokens;
	}

	public void merge(LocationContents contents) {
		nodes.addAll(contents.getNodes());
		tokens.addAll(contents.getTokens());
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}
	
	
}
