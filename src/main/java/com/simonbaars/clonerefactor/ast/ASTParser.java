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
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.Range;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.ListMap;
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
		final Chain buildingChains = new Chain();
		final List<Chain> clones = new ArrayList<Chain>();
		findChains(lastLoc, buildingChains, clones);
		System.out.println(Arrays.toString(clones.toArray()));
	}

	private static void findChains(Location lastLoc, Chain buildingChains, List<Chain> clones) {
		Chain newClones = collectClones(lastLoc);
		if(newClones.size()>=1)
			buildingChains = makeValid(buildingChains, newClones, clones); //Because of the recent additions the current chain may be invalidated
		if(lastLoc.getPrevLine()!=null) {
			if(lastLoc.getPrevLine().getFile()!=lastLoc.getFile()) {
				buildingChains.getChain().clear();
			}
			findChains(lastLoc.getPrevLine(), buildingChains, clones); //I can also do this non recursively, but this looks nice :D
		}
	}

	
	private static Chain makeValid(Chain oldClones, Chain newClones, List<Chain> clones) {
		Map<Location /*oldClones*/, Location /*newClones*/> validChains = oldClones.getChain().stream().filter(e -> newClones.getChain().contains(e.getPrevLine())).collect(Collectors.toMap(e -> e, e -> e.getPrevLine()));
		
		if(validChains.size()!=oldClones.size()) {
			checkValidClones(oldClones, oldClones.getChain().stream().filter(e -> !newClones.getChain().contains(e.getPrevLine())).collect(Collectors.toList()), clones);
		}
		
		for(Entry<Location, Location> validChain : validChains.entrySet()) {
			validChain.getValue().setEndLine(validChain.getKey().getEndLine());
			validChain.getValue().setAmountOfLines(validChain.getKey().getAmountOfLines()+1);
		}
		
		mergeClones(newClones);
		
		return newClones;
	}

	private static Chain mergeClones(Chain newClones) {
		outerloop: for(int i = 0; i<newClones.size(); i++) {
			Location loc1 = newClones.getChain().get(i);
			for(int j = i+1; j<newClones.size(); j++) {
				Location loc2 = newClones.getChain().get(j);
				if(loc1.getFile() == loc2.getFile()) {
					int beginLineOne = loc1.getBeginLine();
					int endLineOne = loc1.getEndLine();
					int beginLineTwo = loc2.getBeginLine();
					int endLineTwo = loc2.getEndLine();
					if(overlap(beginLineOne, endLineOne, beginLineTwo, endLineTwo)) {
						if(endLineOne>endLineTwo) {
							newClones.getChain().remove(i);
							loc2.setBeginLine(loc1.getBeginLine());
							i--;
							continue outerloop;
						} else {
							newClones.getChain().remove(j);
							loc1.setBeginLine(loc2.getBeginLine());
							j--;
						}
					}
				}
			}
		}
		return newClones;
	}
	
	public static boolean overlap(int x1, int y1, int x2, int y2) {
		return x1 <= y2 && y1 <= x2;
	}

	private static void checkValidClones(Chain oldClones, List<Location> endedClones, List<Chain> clones) {
		ListMap<Integer /*Chain size*/, Location /* Clones */> cloneList = new ListMap<>();
		oldClones.getChain().stream().filter(e -> e.getAmountOfLines() > MIN_AMOUNT_OF_LINES).forEach(e -> cloneList.addTo(e.getAmountOfLines(), e));
		for(List<Location> l : cloneList.values()) {
			if(l.stream().anyMatch(e -> endedClones.contains(e))) {
				clones.add(new Chain(l));
				continue;
			}
		}
	}
	
	private static Chain collectClones(Location lastLoc) {
		Chain c = new Chain();
		while(lastLoc!=null) {
			c.add(lastLoc);
			lastLoc = lastLoc.getClone();
		}
		return c;
	}

	/**
	 * AST PARSING
	 * @return 
	 */
	
	private static final Location calculateLineReg(List<File> javaFiles) {
		final Map<LineTokens, Location> lineReg = new HashMap<>();
		Location l = null;
		final CompilationUnitReg r = new CompilationUnitReg();
		for(File file : javaFiles) {
			try {
				l = parseClassFile(lineReg, file, r);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			r.reset();
		}
		return l;
	}

	private static Location parseClassFile(final Map<LineTokens, Location> lineReg, File file, CompilationUnitReg r)
			throws FileNotFoundException {
		final ParseResult<CompilationUnit> pr = new JavaParser().parse(file);
		CompilationUnit cu = pr.getResult().get();
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
