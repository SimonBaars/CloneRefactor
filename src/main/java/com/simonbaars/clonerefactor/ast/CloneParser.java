package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.detection.CloneDetection;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneParser implements Parser {

	NodeParser astParser = new NodeParser();
	
	public List<Sequence> parse(List<File> javaFiles) {
		final ListMap<Integer, Location> cloneReg = new ListMap<>();
		Location lastLoc = calculateLineReg(javaFiles, cloneReg);
		if(lastLoc!=null)
			return new CloneDetection().findChains(lastLoc, cloneReg);
		return new ArrayList<>();
	}

	private final Location calculateLineReg(List<File> javaFiles, ListMap<Integer, Location> cloneReg) {
		Location l = null;
		for(File file : javaFiles) {
			try {
				l = setIfNotNull(l, parseClassFile(file, cloneReg));
			} catch (FileNotFoundException e) {
				return null;
			}
		}
		return l;
	}

	private Location parseClassFile(File file, ListMap<Integer, Location> cloneReg) throws FileNotFoundException {
		final ParseResult<CompilationUnit> pr = new JavaParser().parse(file);
		if(pr.isSuccessful() && pr.getResult().isPresent()) {
			CompilationUnit cu = pr.getResult().get();
			Location l = astParser.extractLinesFromAST(null, file, cu, cloneReg);
			astParser.addLineTokensToReg(l, cloneReg);
			return l;
		}
		return null;
	}
}
