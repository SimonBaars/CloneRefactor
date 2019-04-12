package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.nodeTypes.NodeWithIdentifier;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.simonbaars.clonerefactor.model.LineTokens;
import com.simonbaars.clonerefactor.model.Location;

public class NodeParser implements Parser {
	final Map<LineTokens, Location> lineReg = new HashMap<>();
	
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
		//System.out.println("Parsing "+n.getClass()+" as "+n);
		Optional<Range> range = n.getRange();
		Location thisLocation = prevLocation;
		if(range.isPresent()) {
			int line = range.get().begin.line;
			if(prevLocation!=null && prevLocation.getLine() != line) {
				addLineTokensToReg(prevLocation);
				thisLocation = new Location(file, line, prevLocation);
				prevLocation.setNextLine(thisLocation);
			} else if(prevLocation==null) thisLocation = new Location(file, line, prevLocation);
			thisLocation.getTokens().add(n);
			thisLocation.incrementTokens();
			
		}
		return thisLocation;
	}

	public Location addLineTokensToReg(Location location) {
		if(lineReg.containsKey(location.getTokens())) {
			location.setClone(lineReg.get(location.getTokens()));
			lineReg.put(location.getTokens(), location);
		} else {
			lineReg.put(location.getTokens(), location);
		}
		return location;
	}
}
