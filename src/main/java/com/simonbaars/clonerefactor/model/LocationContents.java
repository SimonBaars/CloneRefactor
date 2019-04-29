package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.JavaToken;
import com.github.javaparser.JavaToken.Category;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithImplements;
import com.simonbaars.clonerefactor.compare.CloneType;
import com.simonbaars.clonerefactor.compare.Compare;
import com.simonbaars.clonerefactor.exception.NoTokensException;

public class LocationContents {
	private Range range;
	private final List<Node> nodes;
	private final List<JavaToken> tokens;
	private final List<Compare> compare;
	
	private static final Category[] NO_TOKEN = {Category.COMMENT, Category.EOL, Category.WHITESPACE_NO_EOL};
	
	public LocationContents() {
		this.nodes = new ArrayList<>();
		this.tokens = new ArrayList<>();
		this.compare = new ArrayList<>();
	}

	public LocationContents(LocationContents contents) {
		this.range = contents.range;
		this.nodes = new ArrayList<>(contents.getNodes());
		this.tokens = new ArrayList<>(contents.getTokens());
		this.compare = new ArrayList<>(contents.getCompare());
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
		return compare.equals(((LocationContents)o).compare);
	}
	
	public List<Node> getNodesForCompare(){
		return getNodesForCompare(getNodes());
	}
	
	public List<Node> getNodesForCompare(List<Node> parents){
		List<Node> nodes = new ArrayList<>();
		for(Node node : parents) {
			Optional<Range> rangeOptional = node.getRange();
			if(rangeOptional.isPresent() && range.contains(rangeOptional.get()))
				nodes.add(node);
			nodes.addAll(getNodesForCompare(node.getChildNodes()));
		}
		return nodes;
	}
	
	public String getEffectiveTokenTypes() {
		return getEffectiveTokens().map(e -> "["+e.asString()+", "+e.getCategory()+", "+e.getKind()+"]").collect(Collectors.joining(", "));
	}
	
	public String getNodeTypes() {
		return getNodesForCompare().stream().map(e -> "["+e.toString()+", "+e.getClass().getName()+"]").collect(Collectors.joining(", "));
	}
	
	public int getAmountOfTokens() {
		return new Long(getEffectiveTokens().count()).intValue();
	}

	private Stream<JavaToken> getEffectiveTokens() {
		return tokens.stream().filter(e -> Arrays.stream(NO_TOKEN).noneMatch(c -> c.equals(e.getCategory())));
	}
	
	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		for(Compare node : compare) {
			result = prime*result*node.getHashCode();
		}
		return result;
	}

	@Override
	public String toString() {
		return getEffectiveTokens().map(e -> e.asString()).collect(Collectors.joining());
	}

	public Range addTokens(Node n, TokenRange tokenRange, Range validRange) {
		for(JavaToken token : tokenRange) {
			Optional<Range> r = token.getRange();
			if(r.isPresent()) {
				if(!validRange.contains(r.get())) break;
				tokens.add(token);
				if(n instanceof NodeWithImplements && token.asString().equals("{")) break; // We cannot exclude the body of class files, this is a workaround.
			}
		}
		if(tokens.isEmpty())
			throw new NoTokensException(n, tokenRange, validRange);
		range = new Range(tokens.get(0).getRange().get().begin, tokens.get(tokens.size()-1).getRange().get().end);
		getNodesForCompare(Arrays.asList(n)).forEach(e -> getCompare().add(Compare.create(e, CloneType.TYPE1)));
		return range; 
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

	public Set<Integer> getEffectiveLines() {
		return getEffectiveTokens().map(e -> e.getRange().get().begin.line).collect(Collectors.toSet());
	}

	public List<Compare> getCompare() {
		return compare;
	}
	
	
}
