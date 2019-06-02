package com.simonbaars.clonerefactor.model.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.ast.interfaces.HasCompareList;
import com.simonbaars.clonerefactor.compare.Compare;
import com.simonbaars.clonerefactor.compare.CompareFalse;
import com.simonbaars.clonerefactor.compare.CompareLiteral;
import com.simonbaars.clonerefactor.compare.CompareMethodCall;
import com.simonbaars.clonerefactor.compare.CompareVariable;
import com.simonbaars.clonerefactor.metrics.enums.CloneContents;
import com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType;
import com.simonbaars.clonerefactor.metrics.enums.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.FiltersTokens;
import com.simonbaars.clonerefactor.settings.Scope;
import com.simonbaars.clonerefactor.settings.Settings;

public class LocationContents implements FiltersTokens, HasRange, HasCompareList, RequiresNodeContext {
	private Range range;
	private final List<Node> nodes;
	private final List<JavaToken> tokens;
	private final List<Compare> compare;
	
	private ContentsType contentsType;
	
	public LocationContents() {
		this.nodes = new ArrayList<>();
		this.tokens = new ArrayList<>();
		this.compare = new ArrayList<>();
	}

	public LocationContents(LocationContents contents) {
		this(contents, contents.range);
	}

	public LocationContents(Node...nodes) {
		this.nodes = new ArrayList<>();
		this.tokens = new ArrayList<>();
		this.compare = new ArrayList<>();
		for(Node n : nodes) {
			this.tokens.addAll(calculateTokensFromNode(n));
			this.nodes.add(n);
			if(Settings.get().getScope()!=Scope.ALL && (getMethod(n)==null || (Settings.get().getScope() == Scope.METHODBODYONLY && n instanceof MethodDeclaration)))
				this.compare.add(new CompareFalse());
			else if(!Settings.get().useLiteratureTypeDefinitions()) createComparablesByNode(tokens, n); 
		}
		this.range = getRange(this.tokens);
	}

	public LocationContents(LocationContents contents, Range r) {
		this.range = r;
		this.nodes = new ArrayList<>(contents.getNodes());
		this.tokens = new ArrayList<>(contents.getTokens());
		this.compare = new ArrayList<>(contents.getCompare());
	}

	public LocationContents(Range r) {
		this.range = r;
		this.nodes = new ArrayList<>();
		this.tokens = new ArrayList<>();
		this.compare = new ArrayList<>();
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
		LocationContents other = (LocationContents)o;
		return Settings.get().useLiteratureTypeDefinitions() && compare.isEmpty() ? tokens.equals(other.tokens) : compare.equals(other.compare);
	}
	
	public String getEffectiveTokenTypes() {
		return getTokens().stream().map(e -> "["+e.asString()+", "+e.getCategory()+", "+e.getKind()+"]").collect(Collectors.joining(", "));
	}

	public int getAmountOfTokens() {
		return getTokens().size();
	}

	@Override
	public int hashCode() {
		if(Settings.get().useLiteratureTypeDefinitions() && compare.isEmpty()) return tokens.hashCode();
		int prime = 31;
		int result = 1;
		for(Compare node : compare) {
			result = prime*result*node.hashCode();
		}
		return result;
	}

	@Override
	public String toString() {
		return getTokens().stream().map(JavaToken::asString).collect(Collectors.joining());
	}

	public List<JavaToken> getTokens() {
		return tokens;
	}

	public void merge(LocationContents contents) {
		nodes.addAll(contents.getNodes());
		tokens.addAll(contents.getTokens());
		compare.addAll(contents.getCompare());
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public Set<Integer> getEffectiveLines() {
		return getTokens().stream().map(e -> e.getRange().get().begin.line).collect(Collectors.toSet());
	}

	public List<Compare> getCompare() {
		return compare;
	}

	public ContentsType getContentsType() {
		return contentsType;
	}
	
	public void setMetrics(CloneContents c) {
		this.contentsType = c.get(this);
	}
	
	@SuppressWarnings("rawtypes")
	public final List<Compare> getType2Variable(Class...of) {
		return compare.stream().filter(e -> Arrays.stream(of).anyMatch(f -> f.equals(e.getClass()))).map(e -> (Compare)e).collect(Collectors.toList());
	}

	public List<Compare> getType2Comparables() {
		return getType2Variable(CompareLiteral.class, CompareMethodCall.class, CompareVariable.class);
	}

	public String compareTypes() {
		return getCompare().stream().map(e -> e.getClass().getName() +" ("+e.toString()+")").collect(Collectors.joining(","));
	}

	public void stripToRange() {
		getNodes().removeIf(e -> {Range r = getRange(e); return r.isBefore(getRange().begin) || r.isAfter(getRange().end);});
		getCompare().removeIf(e -> e.getRange().isBefore(getRange().begin) || e.getRange().isAfter(getRange().end));
		getTokens().removeIf(e -> e.getRange().get().isBefore(getRange().begin) || e.getRange().get().isAfter(getRange().end));
	}
	
	public void isValid() {
		if(tokens.stream().anyMatch(e -> !range.contains(e.getRange().get())))
			throw new IllegalStateException("Invalid Token "+this);
		if(hasDuplicate(nodes))
			throw new IllegalStateException("Invalid Node "+this);
	}
	
	public<T> boolean hasDuplicate(List<T> list) {
		Set<T> tempSet = new HashSet<>();
		for(T t : list)
			if (!tempSet.add(t)) return true;
		return false;
	}


	public void setTokens(TokenRange tokenRange) {
		StreamSupport.stream(tokenRange.spliterator(), false).filter(this::isComparableToken).forEach(e -> getTokens().add(e));
	}
}
