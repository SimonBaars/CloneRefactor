package com.simonbaars.clonerefactor.detection.model.location;

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
import com.simonbaars.clonerefactor.context.analyze.CloneContents;
import com.simonbaars.clonerefactor.context.enums.ContentsType;
import com.simonbaars.clonerefactor.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.datatype.map.ListMap;
import com.simonbaars.clonerefactor.detection.interfaces.HasSettings;
import com.simonbaars.clonerefactor.graph.compare.Compare;
import com.simonbaars.clonerefactor.graph.compare.CompareLiteral;
import com.simonbaars.clonerefactor.graph.compare.CompareMethodCall;
import com.simonbaars.clonerefactor.graph.compare.CompareOutOfScope;
import com.simonbaars.clonerefactor.graph.compare.CompareTokens;
import com.simonbaars.clonerefactor.graph.compare.CompareVariable;
import com.simonbaars.clonerefactor.graph.interfaces.CalculatesLineSize;
import com.simonbaars.clonerefactor.graph.interfaces.HasCompareList;
import com.simonbaars.clonerefactor.settings.Scope;
import com.simonbaars.clonerefactor.settings.Settings;

public class LocationContents extends HasSettings implements HasRange, HasCompareList, RequiresNodeContext, CalculatesLineSize
{
	private Range range;
	private final List<Node> nodes;
	private final List<JavaToken> tokens;
	private final List<Compare> compare;
	
	private ContentsType contentsType;
	
	public LocationContents(Settings settings) {
		super(settings);
		this.nodes = new ArrayList<>();
		this.tokens = new ArrayList<>();
		this.compare = new ArrayList<>();
	}

	public LocationContents(Settings settings, LocationContents contents) {
		this(settings, contents, contents.range);
	}

	public LocationContents(Settings settings, Node...nodes) {
		this(settings);
		if(nodes.length == 0) throw new IllegalStateException("Must have at least one node!");
		for(Node n : nodes) {
			List<JavaToken> nodeTokens = calculateTokensFromNode(n);
			this.tokens.addAll(nodeTokens);
			this.nodes.add(n);
			if(settings.getScope()!=Scope.ALL && (!getMethod(n).isPresent() || (settings.getScope() == Scope.METHODBODYONLY && n instanceof MethodDeclaration)))
				this.compare.add(new CompareOutOfScope(getRange(n)));
			else if(!settings.useLiteratureTypeDefinitions()) createComparablesByNode(settings, tokens, n);
			else this.compare.add(new CompareTokens(filterTokensForCompare(settings.getCloneType(), nodeTokens), getRange(nodeTokens)));
		}
		determineRange();
	}

	public LocationContents(Settings settings, LocationContents contents, Range r) {
		//if(contents.nodes.isEmpty()) throw new IllegalStateException("Must have at least one node!");
		super(settings);
		this.range = r;
		this.nodes = new ArrayList<>(contents.getNodes());
		this.tokens = new ArrayList<>(contents.getTokens());
		this.compare = new ArrayList<>(contents.getCompare());
	}

	public LocationContents(Settings s, Range r) {
		this(s);
		this.range = r;
	}

	public List<Node> getNodes() {
		return nodes;
	}
	
	public List<Node> getTopLevelNodes(){
		ListMap<Integer, Node> nodeMap = new ListMap<>();
		for(Node n : getNodes()) {
			nodeMap.addTo(nodeDepth(n), n);
		}
		return nodeMap.entrySet().stream().reduce((a, b) -> a.getKey() > b.getKey() ? b : a).get().getValue();
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
	
	public String getEffectiveTokenTypes() {
		return getTokens().stream().map(e -> "["+e.asString()+", "+e.getCategory()+", "+e.getKind()+"]").collect(Collectors.joining(", "));
	}

	public int getAmountOfTokens() {
		return getTokens().size();
	}

	@Override
	public int hashCode() {
		return compare.hashCode();
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
		determineRange();
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public Set<Integer> effectiveLines() {
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
		getCompare().removeIf(compare -> getNodes().stream().noneMatch(node -> node == compare.getBelongsToStatement()));
		getTokens().removeIf(e -> e.getRange().get().isBefore(getRange().begin) || e.getRange().get().isAfter(getRange().end));
		if(nodes.size() == 0) throw new IllegalStateException("Must have at least one node! "+getRange());
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

	public void determineRange() {
		this.setRange(getRange(this.getTokens()));
	}

	public int getAmountOfLines() {
		return lineSize(tokens);
	}
}
