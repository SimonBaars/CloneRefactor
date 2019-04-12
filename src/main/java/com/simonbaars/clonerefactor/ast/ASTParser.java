package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.CompilationUnitReg;
import com.simonbaars.clonerefactor.model.LineTokens;
import com.simonbaars.clonerefactor.model.Location;

public class ASTParser implements Parser {
	public Location parseToken(Location prevLocation, File file,
			Node t, ListMap<Integer, Location> cloneReg) {
		Optional<Range> range = t.getRange();
		Location l = null;
		if(range.isPresent()) {
			int line = range.get().begin.line;
			if(r.lastLineNumberExists() && r.getLastLineNumber()!=line)
				l = addLineTokensToReg(lineReg, file, r, r.getLastLineNumber(), cloneReg);
			r.visitLine(line);
			r.getThisLine().add(t);
			//if(!it.hasNext())
			//	l = addLineTokensToReg(lineReg, file, r, line, cloneReg);
		}
		return l;
	}

	private Location addLineTokensToReg(final Map<LineTokens, Location> lineReg, File file,
			final CompilationUnitReg r, int line, ListMap<Integer, Location> cloneReg) {
		LineTokens l = new LineTokens(r.getThisLine());
		Location location = new Location(file, line, l.size(), l.hashCode());
		cloneReg.addTo(location.getTokenHash(), location);
		if(lineReg.containsKey(l)) {
			location.setClone(lineReg.get(l));
			lineReg.put(l, location);
		} else {
			lineReg.put(l, location);
		}
		r.nextLine(location);
		return location;
	}
	
	public Location extractLinesFromAST(Location prevLocation, File file, final CompilationUnitReg r, Node n, ListMap<Integer, Location> cloneReg) {
		prevLocation = parseToken(prevLocation, file, r,  n, cloneReg);
		for (Node child : n.getChildNodes()) {
			prevLocation = setIfNotNull(prevLocation, extractLinesFromAST(prevLocation, file, r, child, cloneReg));
		}
		return prevLocation;
	}
}
