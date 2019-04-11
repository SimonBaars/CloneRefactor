package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.CompilationUnitReg;
import com.simonbaars.clonerefactor.model.LineTokens;
import com.simonbaars.clonerefactor.model.Location;

public class TokenParser implements Parser {
	public Location parseToken(final Map<LineTokens, Location> lineReg, File file, final CompilationUnitReg<JavaToken> r,
			Iterator<JavaToken> it, ListMap<Integer, Location> cloneReg) {
		JavaToken t = it.next();
		Optional<Range> range = t.getRange();
		Location l = null;
		if(range.isPresent()) {
			int line = range.get().begin.line;
			if(r.lastLineNumberExists() && r.getLastLineNumber()!=line)
				l = addLineTokensToReg(lineReg, file, r, r.getLastLineNumber(), cloneReg);
			r.visitLine(line);
			r.getThisLine().add(t);
			if(!it.hasNext())
				l = addLineTokensToReg(lineReg, file, r, line, cloneReg);
		}
		return l;
	}

	private Location addLineTokensToReg(final Map<LineTokens, Location> lineReg, File file,
			final CompilationUnitReg<JavaToken> r, int line, ListMap<Integer, Location> cloneReg) {
		LineTokens<JavaToken> l = new LineTokens(r.getThisLine());
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
	
	public Location extractLinesFromAST(final Map<LineTokens, Location> lineReg, File file,
			CompilationUnitReg<JavaToken> r, ListMap<Integer, Location> cloneReg, CompilationUnit cu) {
		Location l = null;
		for (Iterator<JavaToken> it = cu.getTokenRange().get().iterator(); it.hasNext();) {
			l = setIfNotNull(l, parseToken(lineReg, file, r, it, cloneReg));
		}
		return l;
	}
}
