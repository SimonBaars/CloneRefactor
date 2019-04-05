package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.model.Chain;
import com.simonbaars.clonerefactor.model.CompilationUnitReg;
import com.simonbaars.clonerefactor.model.LineTokens;
import com.simonbaars.clonerefactor.model.Location;

public class ASTParser {
	private static final int MIN_AMOUNT_OF_LINES = 6;
	private static final int MIN_AMOUNT_OF_TOKENS = 50;
	
	public static void parse(List<File> javaFiles) {
		System.out.println("Start parse");
		Location lastLoc = calculateLineReg(javaFiles);
		final List<Chain> buildingChains = new ArrayList<Chain>();
		final List<Chain> clones = new ArrayList<Chain>();
		findChains(lastLoc, buildingChains, clones);
	}

	private static void findChains(Location lastLoc, List<Chain> buildingChains, List<Chain> clones) {
		buildingChains.add(new Chain());
		collectClones(lastLoc, buildingChains);
		if(buildingChains.size()>=2)
			makeValid(buildingChains, clones); //Because of the recent additions the current chain may be invalidated
		if(lastLoc.getPrevLine()!=null)
			findChains(lastLoc, buildingChains, clones); //I can also do this non recursively, but this looks nice :D
	}

	
	private static void makeValid(List<Chain> buildingChains, List<Chain> clones) {
		List<Location> existingClones = buildingChains.get(buildingChains.size()-2).getChain();
		List<Location> newClones = buildingChains.get(buildingChains.size()-1).getChain();
		List<Location> validChains = existingClones.stream().map(e -> e.getPrevLine()).collect(Collectors.toList());
		
		List<Location> newChains = newClones.stream().filter(e -> !validChains.contains(e)).collect(Collectors.toList());
		validChains.removeIf(e -> !newChains.contains(e)); //These are the chains that are finished, we should check if we can turn them into clones.
		if(newChains.size()!=existingClones.size())
			detectValidClones(buildingChains, clones, validChains);
		if(newChains.size() == 1) {
			buildingChains.clear();
			buildingChains.add(new Chain(newClones));
		}
	}

	private static void detectValidClones(List<Chain> buildingChains, List<Chain> clones, List<Location> endedChains) {
		List<Chain> potentialClones = new ArrayList<Chain>();
		ListIterator<Chain> li = buildingChains.listIterator(buildingChains.size()-1); // Reverse order iterator on buildingChains
		while(li.hasPrevious()) {
			Chain prev = li.previous();
			for(Location l : prev.getChain()) {
				if(endedChains.contains(l.getPrevLine())) {
					l.setEndLine(l.getPrevLine().getLine());
					endedChains.remove(l.getPrevLine());
					endedChains.add(l);
				}
			}
		}
		checkForClones(clones, endedChains);
	}

	private static void collectClones(Location lastLoc, List<Chain> buildingChains) {
		last(buildingChains).add(lastLoc);
		if(lastLoc.getClone()!=null)
			collectClones(lastLoc.getClone(), buildingChains);
	}

	private static Chain last(List<Chain> buildingChains) {
		return buildingChains.get(buildingChains.size()-1);
	}

	/**
	 * AST PARSING
	 * @return 
	 */
	
	private static final Location calculateLineReg(List<File> javaFiles) {
		final Map<LineTokens, Location> lineReg = new HashMap<>();
		Location l = null;
		for(File file : javaFiles) {
			try {
				l = parseClassFile(lineReg, file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return l;
	}

	private static Location parseClassFile(final Map<LineTokens, Location> lineReg, File file)
			throws FileNotFoundException {
		final ParseResult<CompilationUnit> pr = new JavaParser().parse(file);
		CompilationUnit cu = pr.getResult().get();
		final CompilationUnitReg r = new CompilationUnitReg();
		Location l = null;
		for (Iterator<Node> it = cu.stream().iterator(); it.hasNext();) {
			l = parseToken(lineReg, file, r, it);
		}
		return l;
	}

	private static Location parseToken(final Map<LineTokens, Location> lineReg, File file, final CompilationUnitReg r,
			Iterator<Node> it) {
		Node t = it.next();
		Optional<Range> range = t.getRange();
		Location l = null;
		if(range.isPresent()) {
			int line = range.get().begin.line;
			if(!it.hasNext() || (r.lastLineNumberExists() && r.getLastLineNumber()!=line)) {
				l = addLineTokensToReg(lineReg, file, r, it, line);
			}
			r.visitLine(line);
			r.getThisLine().add(t);
		}
		return l;
	}

	private static Location addLineTokensToReg(final Map<LineTokens, Location> lineReg, File file,
			final CompilationUnitReg r, Iterator<Node> it, int line) {
		int finishedLine = line; // Line to be scanned for clones
		if(it.hasNext())
			finishedLine = r.getLastLineNumber();
		LineTokens l = new LineTokens(r.getThisLine());
		Location location = new Location(file, finishedLine);
		//System.out.println("Line = "+location+", l = "+l);
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
