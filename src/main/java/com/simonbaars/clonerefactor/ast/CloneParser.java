package com.simonbaars.clonerefactor.ast;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import com.simonbaars.clonerefactor.detection.CloneDetection;
import com.simonbaars.clonerefactor.metrics.MetricCollector;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.LocationHolder;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneParser implements Parser {

	private NodeParser astParser;
	public final MetricCollector metricCollector = new MetricCollector();
	
	private final ParserConfiguration config = new ParserConfiguration()
			.setLexicalPreservationEnabled(false) //Disabled for now, we'll enable it when we start refactoring.
			.setStoreTokens(true);
	
	public DetectionResults parse(SourceRoot sourceRoot) {
		astParser = new NodeParser(metricCollector);
		Location lastLoc = calculateLineReg(sourceRoot);
		if(lastLoc!=null) {
			List<Sequence> findChains = new CloneDetection().findChains(lastLoc);
			return new DetectionResults(metricCollector.reportClones(findChains), findChains);
		}
		return new DetectionResults();
	}

	private final Location calculateLineReg(SourceRoot sourceRoot) {
		final LocationHolder lh = new LocationHolder();
		try {
			sourceRoot.parse("", config, new SourceRoot.Callback() {
				@Override
				public Result process(Path localPath, Path absolutePath, ParseResult<CompilationUnit> result) {
					CompilationUnit cu = result.getResult().get();
					lh.setLocation(astParser.extractLinesFromAST(lh.getLocation(), cu, cu));
					return Result.DONT_SAVE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lh.getLocation();
	}
}
