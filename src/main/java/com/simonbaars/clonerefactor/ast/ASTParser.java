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
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.CompilationUnitReg;
import com.simonbaars.clonerefactor.model.LineTokens;
import com.simonbaars.clonerefactor.model.Location;

public class ASTParser {
	private static final int MIN_AMOUNT_OF_LINES = 6;
	private static final int MIN_AMOUNT_OF_TOKENS = 10;
	private static final ListMap<Integer, Location> cloneReg = new ListMap<>();
	
	public static List<Sequence> parse(List<File> javaFiles) {
		System.out.println("Start parse");
		Location lastLoc = calculateLineReg(javaFiles);
		final Sequence buildingChains = new Sequence();
		final List<Sequence> clones = new ArrayList<Sequence>();
		findChains(lastLoc, buildingChains, clones);
		return clones;
	}

	private static void findChains(Location lastLoc, Sequence buildingChains, List<Sequence> clones) {
		Sequence newClones = collectClones(lastLoc);
		if(newClones.size()>=1)
			buildingChains = makeValid(lastLoc, buildingChains, newClones, clones); //Because of the recent additions the current sequence may be invalidated
		if(lastLoc.getPrevLine()!=null) {
			if(lastLoc.getPrevLine().getFile()!=lastLoc.getFile()) {
				buildingChains.getSequence().clear();
			}
			findChains(lastLoc.getPrevLine(), buildingChains, clones); //I can also do this non recursively, but this looks nice :D
		}
	}

	
	private static Sequence makeValid(Location lastLoc, Sequence oldClones, Sequence newClones, List<Sequence> clones) {
		Map<Location /*oldClones*/, Location /*newClones*/> validChains = oldClones.getSequence().stream().filter(e -> newClones.getSequence().contains(e.getPrevLine())).collect(Collectors.toMap(e -> e, e -> e.getPrevLine()));
		
		if(validChains.size()!=oldClones.size() && !oldClones.getSequence().isEmpty()) {
			checkValidClones(oldClones, oldClones.getSequence().stream().filter(e -> !newClones.getSequence().contains(e.getPrevLine())).collect(Collectors.toList()), clones);
		}
		
		for(Entry<Location, Location> validChain : validChains.entrySet()) {
			validChain.getValue().setEndLine(validChain.getKey().getEndLine());
			validChain.getValue().setAmountOfLines(validChain.getKey().getAmountOfLines()+1);
		}
		
		if(lastLoc.isLocationParsed(cloneReg)){
			newClones.getSequence().clear();
			newClones.getSequence().addAll(validChains.values());
		}
		//removePreviouslyParsedClones(newClones);
		mergeClones(newClones);
		
		return newClones;
	}

	private static Sequence mergeClones(Sequence newClones) {
		outerloop: for(int i = 0; i<newClones.size(); i++) {
			Location loc1 = newClones.getSequence().get(i);
			for(int j = i+1; j<newClones.size(); j++) {
				Location loc2 = newClones.getSequence().get(j);
				if(loc1.getFile() == loc2.getFile()) {
					int beginLineOne = loc1.getBeginLine()+1;
					int endLineOne = loc1.getEndLine()+1;
					int beginLineTwo = loc2.getBeginLine();
					int endLineTwo = loc2.getEndLine();
					//System.out.println("Overlap "+beginLineOne+", "+endLineOne+", "+beginLineTwo+", "+endLineTwo+", "+overlap(beginLineOne, endLineOne, beginLineTwo, endLineTwo));
					if(overlap(beginLineOne, endLineOne, beginLineTwo, endLineTwo)) {
						if(endLineOne>endLineTwo) {
							newClones.getSequence().remove(i);
							loc2.setEndLine(loc1.getEndLine());
							i--;
							continue outerloop;
						} else {
							newClones.getSequence().remove(j);
							loc1.setEndLine(loc2.getEndLine());
							j--;
						}
					}
				}
			}
		}
		return newClones;
	}
	
	public static boolean overlap(int x1, int y1, int x2, int y2) {
		return x1 <= y2 || y1 <= x2;
	}

	private static void checkValidClones(Sequence oldClones, List<Location> endedClones, List<Sequence> clones) {
		ListMap<Integer /*Sequence size*/, Location /* Clones */> cloneList = new ListMap<>();
		endedClones.stream().filter(e -> e.getAmountOfLines() > MIN_AMOUNT_OF_LINES).forEach(e -> cloneList.addTo(e.getAmountOfLines(), e));
		for(List<Location> l : cloneList.values()) {
			if(l.stream().anyMatch(e -> endedClones.contains(e))) {
				for(Location l2 : oldClones.getSequence()) {
					if(l.get(0)!= l2 && l2.getAmountOfLines()>=l.get(0).getAmountOfLines()) {
						l.add(new Location(l2.getFile(), l2.getBeginLine(), findActualEndLine(l2, l.get(0).getAmountOfLines())/*l2.getEndLine()*/, l.get(0).getAmountOfLines(), l.get(0).getAmountOfTokens()));
					}
				}
				//System.out.println("ADDING SEQUENCE "+new Sequence(l));
				clones.add(new Sequence(l));
				continue;
			}
		}
	}
	
	private static int findActualEndLine(Location l2, int amountOfLines) {
		//System.out.println(l2.getBeginLine());
		if(amountOfLines>0 && l2.getNextLine()!=null && l2.getNextLine().getFile() == l2.getFile())
			return findActualEndLine(l2.getNextLine(), amountOfLines-1);
		return l2.getBeginLine();
	}

	private static Sequence collectClones(Location lastLoc) {
		Sequence c = new Sequence();
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
		Location location = new Location(file, finishedLine, l.size(), l.hashCode());
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
