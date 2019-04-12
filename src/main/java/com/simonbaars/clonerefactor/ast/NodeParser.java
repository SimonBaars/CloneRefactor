package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.LineTokens;
import com.simonbaars.clonerefactor.model.Location;

public class NodeParser implements Parser {
	final Map<LineTokens, Location> lineReg = new HashMap<>();
	
	public Location extractLinesFromAST(Location prevLocation, File file, Node n, ListMap<Integer, Location> cloneReg) {
		prevLocation = parseToken(prevLocation, file,  n, cloneReg);
		for (Node child : n.getChildNodes()) {
			prevLocation = setIfNotNull(prevLocation, extractLinesFromAST(prevLocation, file, child, cloneReg));
		}
		return prevLocation;
	}
	
	public Location parseToken(Location prevLocation, File file, Node t, ListMap<Integer, Location> cloneReg) {
		Optional<Range> range = t.getRange();
		Location thisLocation = prevLocation;
		if(range.isPresent()) {
			int line = range.get().begin.line;
			if(prevLocation!=null && prevLocation.getLine() != line) {
				addLineTokensToReg(prevLocation, cloneReg);
				thisLocation = new Location(file, line, prevLocation);
				prevLocation.setNextLine(thisLocation);
			} else if(prevLocation==null) thisLocation = new Location(file, line, prevLocation);
			thisLocation.getTokens().add(t);
			thisLocation.incrementTokens();
			
		}
		return thisLocation;
	}

	public Location addLineTokensToReg(Location location, ListMap<Integer, Location> cloneReg) {
		cloneReg.addTo(location.getTokenHash(), location);
		if(lineReg.containsKey(location.getTokens())) {
			location.setClone(lineReg.get(location.getTokens()));
			lineReg.put(location.getTokens(), location);
		} else {
			lineReg.put(location.getTokens(), location);
		}
		return location;
	}
}
