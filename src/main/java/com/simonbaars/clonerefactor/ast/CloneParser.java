package com.simonbaars.clonerefactor.ast;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.simonbaars.clonerefactor.detection.CloneDetection;
import com.simonbaars.clonerefactor.metrics.MetricCollector;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneParser implements Parser {

	private NodeParser astParser;
	public final MetricCollector metricCollector = new MetricCollector();
	
	public DetectionResults parse(List<CompilationUnit> list) {
		astParser = new NodeParser(metricCollector);
		Location lastLoc = calculateLineReg(list);
		if(lastLoc!=null) {
			List<Sequence> findChains = new CloneDetection().findChains(lastLoc);
			return new DetectionResults(metricCollector.reportClones(findChains), findChains);
		}
		return new DetectionResults();
	}

	private final Location calculateLineReg(List<CompilationUnit> list) {
		Location l = null;
		for(CompilationUnit cu : list) {
			l = astParser.extractLinesFromAST(l, cu, cu);;
		}
		return l;
	}
}
