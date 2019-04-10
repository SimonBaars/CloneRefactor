package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.detection.CloneDetection;
import com.simonbaars.clonerefactor.model.CompilationUnitReg;
import com.simonbaars.clonerefactor.model.LineTokens;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class ASTParser {

	public static List<Sequence> parse(List<File> javaFiles) {
		final ListMap<Integer, Location> cloneReg = new ListMap<>();
		Location lastLoc = calculateLineReg(javaFiles, cloneReg);
		if(lastLoc!=null)
			return new CloneDetection().findChains(lastLoc, cloneReg);
		return new ArrayList<>();
	}

	private static final Location calculateLineReg(List<File> javaFiles, ListMap<Integer, Location> cloneReg) {
		final Map<LineTokens, Location> lineReg = new HashMap<>();
		Location l = null;
		final CompilationUnitReg r = new CompilationUnitReg();
		for(File file : javaFiles) {
			try {
				l = setIfNotNull(l, parseClassFile(lineReg, file, r, cloneReg));
			} catch (FileNotFoundException e) {
				return null;
			}
			r.reset();
		}
		return l;
	}

	private static Location setIfNotNull(Location l, Location parseClassFile) {
		return parseClassFile == null ? l : parseClassFile;
	}

	private static Location parseClassFile(final Map<LineTokens, Location> lineReg, File file, CompilationUnitReg r, ListMap<Integer, Location> cloneReg) throws FileNotFoundException {
		final ParseResult<CompilationUnit> pr = new JavaParser().parse(file);
		if(pr.isSuccessful() && pr.getResult().isPresent()) {
			CompilationUnit cu = pr.getResult().get();
			Location l = null;
			for (Iterator<Node> it = cu.stream().iterator(); it.hasNext();) {
				l = parseToken(lineReg, file, r, it, cloneReg);
			}
			return l;
		}
		return null;
	}

	private static Location parseToken(final Map<LineTokens, Location> lineReg, File file, final CompilationUnitReg r,
			Iterator<Node> it, ListMap<Integer, Location> cloneReg) {
		Node t = it.next();
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

	private static Location addLineTokensToReg(final Map<LineTokens, Location> lineReg, File file,
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
}
