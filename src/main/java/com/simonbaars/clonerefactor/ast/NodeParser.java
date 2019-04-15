package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.nodeTypes.NodeWithIdentifier;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.LocationContents;

public class NodeParser implements Parser {
	final Map<LocationContents, Location> lineReg = new HashMap<>();
	
	public Location extractLinesFromAST(Location prevLocation, File file, Node n) {
		if(n instanceof ImportDeclaration || n instanceof Expression || n instanceof Modifier || n instanceof NodeWithIdentifier)
			return prevLocation;
		if(!(n instanceof CompilationUnit))
			prevLocation = parseToken(prevLocation, file,  n);
		for (Node child : childrenToParse(n)) {
			prevLocation = setIfNotNull(prevLocation, extractLinesFromAST(prevLocation, file, child));
		}
		return prevLocation;
	}
	
	@SuppressWarnings("rawtypes")
	public List<Node> childrenToParse(Node parent){
		if(parent instanceof MethodDeclaration) {
			Optional<BlockStmt> body = ((MethodDeclaration)parent).getBody();
			return body.isPresent() ? body.get().getChildNodes() : new ArrayList<>(0);
		} else if(parent instanceof NodeWithBody)
			return ((NodeWithBody)parent).getBody().getChildNodes();
		
		return parent.getChildNodes();
	}
	
	
	public Location parseToken(Location prevLocation, File file, Node n) {
		Range range = getActualRange(n);
		Location thisLocation = prevLocation;
		if(range!=null) {
			thisLocation = new Location(file, prevLocation);
			thisLocation.calculateTokens(n, range);
			if(prevLocation!=null) prevLocation.setNextLine(thisLocation);
			addLineTokensToReg(thisLocation);
			System.out.println("Parsing "+n.getClass().getName()+" as "+thisLocation.getContents()+" with range"+thisLocation.getRange());
		}
		return thisLocation;
	}

	public Location addLineTokensToReg(Location location) {
		if(lineReg.containsKey(location.getContents())) {
			location.setClone(lineReg.get(location.getContents()));
			lineReg.put(location.getContents(), location);
		} else {
			lineReg.put(location.getContents(), location);
		}
		return location;
	}
	
	public boolean hasBody(Node n) {
		return n instanceof NodeWithBody || n instanceof ClassOrInterfaceDeclaration || (n instanceof MethodDeclaration && ((MethodDeclaration)n).getBody().isPresent());
	}
	
	public Statement getBody(Node n) {
		if(n instanceof NodeWithBody)
			return ((NodeWithBody) n).getBody();
		else if (n instanceof MethodDeclaration && ((MethodDeclaration)n).getBody().isPresent())
			return ((MethodDeclaration)n).getBody().get();
		return null;
	}
	
	public Range getActualRange(Node n) {
		Optional<Range> nodeRangeOpt = n.getRange();
		if(nodeRangeOpt.isPresent()) {
			Range nodeRange = nodeRangeOpt.get();
			Statement body = getBody(n);
			if(body!=null) { //If this node has a body we want to subtract its range.
				Optional<Range> bodyRangeOpt = body.getRange();
				if(bodyRangeOpt.isPresent())
					return subtractRange(nodeRange, bodyRangeOpt.get());
			}
			return nodeRange;
		}
		return null;
	}

	private Range subtractRange(Range nodeRange, Range bodyRange) {
		return nodeRange.withEnd(bodyRange.begin);
	}
}
