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

public class ASTParser implements Parser {
	final Map<LineTokens, Location> lineReg = new HashMap<>();
	
	public Location parseToken(Location prevLocation, File file, Node t, ListMap<Integer, Location> cloneReg) {
		Optional<Range> range = t.getRange();
		Location thisLocation = null;
		if(range.isPresent()) {
			int line = range.get().begin.line;
			if(prevLocation.getLine() != line) {
				thisLocation = new Location(file, line, prevLocation);
				prevLocation.setNextLine(thisLocation);
			} else thisLocation = prevLocation;
			if()
			addLineTokensToReg(thisLocation, cloneReg);
		}
		return thisLocation;
	}

	private Location addLineTokensToReg(Location location, ListMap<Integer, Location> cloneReg) {
		cloneReg.addTo(location.getTokenHash(), location);
		if(lineReg.containsKey(location.getTokens())) {
			location.setClone(lineReg.get(location.getTokens()));
			lineReg.put(location.getTokens(), location);
		} else {
			lineReg.put(location.getTokens(), location);
		}
		return location;
	}
	
	public Location extractLinesFromAST(Location prevLocation, File file, Node n, ListMap<Integer, Location> cloneReg, boolean isLast) {
		prevLocation = parseToken(prevLocation, file,  n, cloneReg);
		for (Node child : n.getChildNodes()) {
			prevLocation = setIfNotNull(prevLocation, extractLinesFromAST(prevLocation, file, child, cloneReg));
		}
		return prevLocation;
	}
}
