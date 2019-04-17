package com.simonbaars.clonerefactor.ast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.simonbaars.clonerefactor.detection.CloneDetection;
import com.simonbaars.clonerefactor.metrics.MetricCollector;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneParser implements Parser {

	private NodeParser astParser;
	public final MetricCollector metricCollector = new MetricCollector();
	
	public DetectionResults parse(List<File> javaFiles) {
		astParser = new NodeParser(metricCollector);
		Location lastLoc = calculateLineReg(javaFiles);
		if(lastLoc!=null) {
			List<Sequence> findChains = new CloneDetection().findChains(lastLoc);
			return new DetectionResults(metricCollector.reportClones(findChains), findChains);
		}
		return new DetectionResults();
	}

	private final Location calculateLineReg(List<File> javaFiles) {
		Location l = null;
		for(File file : javaFiles) {
			try {
				l = setIfNotNull(l, parseClassFile(file, l));
			} catch (FileNotFoundException e) {
				return null;
			}
		}
		return l;
	}

	private Location parseClassFile(File file, Location location) throws FileNotFoundException {
		final ParseResult<CompilationUnit> pr = new JavaParser().parse(file);
		if(pr.isSuccessful() && pr.getResult().isPresent()) {
			CompilationUnit cu = pr.getResult().get();
			return astParser.extractLinesFromAST(location, file, cu);
		}
		return null;
	}
}
