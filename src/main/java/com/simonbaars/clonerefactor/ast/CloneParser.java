package com.simonbaars.clonerefactor.ast;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import com.simonbaars.clonerefactor.Settings;
import com.simonbaars.clonerefactor.compare.CloneType;
import com.simonbaars.clonerefactor.detection.CloneDetection;
import com.simonbaars.clonerefactor.detection.Type2Variability;
import com.simonbaars.clonerefactor.detection.Type3Opportunities;
import com.simonbaars.clonerefactor.metrics.MetricCollector;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.model.location.LocationHolder;

public class CloneParser implements Parser {

	private NodeParser astParser;
	public final MetricCollector metricCollector = new MetricCollector();
	
	public DetectionResults parse(SourceRoot sourceRoot, ParserConfiguration config) {
		astParser = new NodeParser(metricCollector);
		Location lastLoc = calculateLineReg(sourceRoot, config);
		if(lastLoc!=null) {
			List<Sequence> findChains = new CloneDetection().findChains(lastLoc);
			doTypeSpecificTransformations(findChains);
			return new DetectionResults(metricCollector.reportClones(findChains), findChains);
		}
		return new DetectionResults();
	}

	private void doTypeSpecificTransformations(List<Sequence> findChains) {
		doType2Transformations(findChains); 
		if (Settings.get().getCloneType() == CloneType.TYPE3) {
			new Type3Opportunities().determineType3Opportunities(findChains);
		}
	}

	private void doType2Transformations(List<Sequence> findChains) {
		if(Settings.get().getCloneType().isNotTypeOne()) {
			IntStream.range(0, findChains.size()).forEach(i -> {
				findChains.addAll(new Type2Variability().determineVariability(findChains.remove(0)));
			});
		}
	}

	private final Location calculateLineReg(SourceRoot sourceRoot, ParserConfiguration config) {
		final LocationHolder lh = new LocationHolder();
		try {
			sourceRoot.parse("", config, new SourceRoot.Callback() {
				@Override
				public Result process(Path localPath, Path absolutePath, ParseResult<CompilationUnit> result) {
					if(result.getResult().isPresent()) {
						CompilationUnit cu = result.getResult().get();
						lh.setLocation(astParser.extractLinesFromAST(lh.getLocation(), cu, cu));
					}
					return Result.DONT_SAVE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lh.getLocation();
	}
}
