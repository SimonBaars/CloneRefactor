package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.FlattenedList;
import com.simonbaars.clonerefactor.datatype.LineBuffer;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.CloneClass;
import com.simonbaars.clonerefactor.model.CompilationUnitReg;
import com.simonbaars.clonerefactor.model.SimilarityReg;

public class ASTParser {
	private static final int MIN_AMOUNT_OF_LINES = 6;
	private static final int MIN_AMOUNT_OF_TOKENS = 50;
	private static final double SIMILARITYTHRESHOLD = 90;
	
	public static void parse(List<File> javaFiles) {
		final Map<File, ListMap<Integer, Node>> tokensOnLine = new HashMap<>();
		final ListMap<File, Integer> sortedDomain = new ListMap<>();
		// Lists in order: List of potential clone classes -> 6 lines in the potential clone class -> List of tokens on the line.
		final List<CloneClass> potentialClones = new ArrayList<>();
		final List<CloneClass> foundCloneClasses = new ArrayList<>();
		for(File file : javaFiles) {
			try {
				final ParseResult<CompilationUnit> pr = new JavaParser().parse(file);
				CompilationUnit cu = pr.getResult().get();
				final CompilationUnitReg r = new CompilationUnitReg();
				for (Iterator<Node> it = cu.stream().iterator(); it.hasNext();) {
					Node t = it.next();
					Optional<Range> range = t.getRange();
					if(range.isPresent()) {
						int line = range.get().begin.line;
						if(!it.hasNext() || (r.lastLineNumberExists() && r.getLastLineNumber()!=line)) {
							int finishedLine = line; // Line to be scanned for clones
							if(it.hasNext())
								finishedLine = r.getLastLineNumber();
							sortedDomain.addTo(file, finishedLine);
							List<Node> nodes = r.getThisFile().get(finishedLine);
							r.getBuffer().addToBuffer(nodes);
							if(r.getBuffer().isValid()) {
								scanForClones(potentialClones, foundCloneClasses, r.getBuffer());
								potentialClones.add(new CloneClass(r.getBuffer().getLines()));
							}
						}
						r.visitLine(line);
						r.getThisFile().addTo(line, t);
					}
				}
				tokensOnLine.put(file, r.getThisFile());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println(Arrays.toString(foundCloneClasses.toArray()));
	}

	private static void scanForClones(List<CloneClass> potentialClones, List<CloneClass> foundCloneClasses, LineBuffer buffer) {
		for(CloneClass potentialClone : potentialClones) {
			if(potentialClone.getCloneClass() == buffer.getLines()) {
				foundCloneClasses.add(new CloneClass(buffer.getLines()));
			}
		}
	}
}
