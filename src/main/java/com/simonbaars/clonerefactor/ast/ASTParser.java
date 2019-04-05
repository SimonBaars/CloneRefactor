package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.event.ListSelectionEvent;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.LineBuffer;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.CloneClass;
import com.simonbaars.clonerefactor.model.CompilationUnitReg;
import com.simonbaars.clonerefactor.model.LineTokens;
import com.simonbaars.clonerefactor.model.Location;

public class ASTParser {
	private static final int MIN_AMOUNT_OF_LINES = 6;
	private static final int MIN_AMOUNT_OF_TOKENS = 50;
	
	public static void parse(List<File> javaFiles) {
		final Map<LineTokens, List<Location>> lineReg = calculateLineReg(javaFiles);
	}

	private static final Map<LineTokens, List<Location>> calculateLineReg(List<File> javaFiles) {
		final Map<LineTokens, List<Location>> lineReg = new HashMap<>();
		for(File file : javaFiles) {
			try {
				parseClassFile(lineReg, file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return lineReg;
	}

	private static void parseClassFile(final Map<LineTokens, List<Location>> lineReg, File file)
			throws FileNotFoundException {
		final ParseResult<CompilationUnit> pr = new JavaParser().parse(file);
		CompilationUnit cu = pr.getResult().get();
		final CompilationUnitReg r = new CompilationUnitReg();
		for (Iterator<Node> it = cu.stream().iterator(); it.hasNext();) {
			parseToken(lineReg, file, r, it);
		}
	}

	private static void parseToken(final Map<LineTokens, List<Location>> lineReg, File file, final CompilationUnitReg r,
			Iterator<Node> it) {
		Node t = it.next();
		Optional<Range> range = t.getRange();
		if(range.isPresent()) {
			int line = range.get().begin.line;
			if(!it.hasNext() || (r.lastLineNumberExists() && r.getLastLineNumber()!=line)) {
				addLineTokensToReg(lineReg, file, r, it, line);
			}
			r.visitLine(line);
			r.getThisLine().add(t);
		}
	}

	private static void addLineTokensToReg(final Map<LineTokens, List<Location>> lineReg, File file,
			final CompilationUnitReg r, Iterator<Node> it, int line) {
		int finishedLine = line; // Line to be scanned for clones
		if(it.hasNext())
			finishedLine = r.getLastLineNumber();
		LineTokens l = new LineTokens(r.getThisLine());
		Location location = new Location(file, finishedLine);
		if(lineReg.containsKey(l)) 
			lineReg.get(l).add(location);
		else lineReg.put(l, Arrays.asList(location));
	}

	private static void scanForClones(List<CloneClass> potentialClones, List<CloneClass> foundCloneClasses, LineBuffer buffer) {
		for(CloneClass potentialClone : potentialClones) {
			if(potentialClone.getCloneClass() == buffer.getLines()) {
				foundCloneClasses.add(new CloneClass(buffer.getLines()));
			}
		}
	}
}
