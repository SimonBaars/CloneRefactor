package com.simonbaars.clonerefactor.ast;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.utils.SourceRoot.Callback.Result;
import com.simonbaars.clonerefactor.SequenceObservable;
import com.simonbaars.clonerefactor.ast.interfaces.SetsIfNotNull;
import com.simonbaars.clonerefactor.detection.CloneDetection;
import com.simonbaars.clonerefactor.detection.interfaces.RemovesDuplicates;
import com.simonbaars.clonerefactor.detection.type2.Type2Variability;
import com.simonbaars.clonerefactor.detection.type3.Type3Opportunities;
import com.simonbaars.clonerefactor.metrics.MetricCollector;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.refactoring.ExtractMethod;
import com.simonbaars.clonerefactor.refactoring.RefactoringStrategy;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.thread.CalculatesTimeIntervals;
import com.simonbaars.clonerefactor.thread.WritesErrors;

public class CloneParser implements SetsIfNotNull, RemovesDuplicates, WritesErrors, CalculatesTimeIntervals {

	private final SequenceObservable seqObservable = new SequenceObservable(); 

	public DetectionResults parse(Path projectRoot, SourceRoot sourceRoot, ParserConfiguration config) {
		MetricCollector metricCollector = new MetricCollector();
		
		final List<CompilationUnit> compilationUnits = createAST(sourceRoot, config);
		
		if(compilationUnits.isEmpty())
			throw new IllegalStateException("Project has no sources!");
				
		return parseProject(projectRoot, sourceRoot, metricCollector, 0, compilationUnits);
	}

	private DetectionResults parseProject(Path projectRoot, SourceRoot sourceRoot, MetricCollector metricCollector,
			int nGenerated, final List<CompilationUnit> compilationUnits) {
		long beginTime = System.currentTimeMillis();
		seqObservable.subscribe(new MetricObserver(metricCollector));
		NodeParser astParser = new NodeParser(metricCollector, seqObservable);
		Location lastLoc = calculateLineReg(astParser, compilationUnits);
		if(lastLoc!=null) {
			List<Sequence> findChains = new CloneDetection(seqObservable).findChains(lastLoc);
			doTypeSpecificTransformations(findChains);
			metricCollector.getMetrics().generalStats.increment("Detection time", interval(beginTime));
			DetectionResults res = new DetectionResults(metricCollector.reportClones(findChains), findChains);
			if(Settings.get().getRefactoringStrategy() != RefactoringStrategy.DONOTREFACTOR) {
				int nGen = new ExtractMethod(projectRoot, sourceRoot.getRoot(), compilationUnits, metricCollector, nGenerated).refactor(findChains);
				System.out.println("Gen "+nGenerated+" of "+nGen);
				if(nGen!=nGenerated)
					res.getMetrics().setChild(parseProject(projectRoot, sourceRoot, new MetricCollector(), nGen, compilationUnits).getMetrics());
			} 
			return res;
		} else throw new IllegalStateException("Project has no usable sources!");
		
	}

	private void doTypeSpecificTransformations(List<Sequence> findChains) {
		doType2Transformations(findChains); 
		if (Settings.get().getCloneType() == CloneType.TYPE3)
			new Type3Opportunities().determineType3Opportunities(findChains);
	}

	private void doType2Transformations(List<Sequence> findChains) {
		if(Settings.get().getCloneType().isNotTypeOne() && !Settings.get().isUseLiteratureTypeDefinitions()) {
			IntStream.range(0, findChains.size()).forEach(i -> {
				List<Sequence> determineVariability = new Type2Variability().determineVariability(findChains.remove(0));
				for(Sequence s : determineVariability) {
					if(removeDuplicatesOf(findChains, s) && s.size()>1)
						findChains.add(s);
				}
			});
		}
	}

	private final Location calculateLineReg(NodeParser astParser, List<CompilationUnit> compilationUnits) {
		Location l = null;
		for(CompilationUnit cu : compilationUnits)
			l = astParser.extractLinesFromAST(l, cu, cu);
		return l;
	}

	private List<CompilationUnit> createAST(SourceRoot sourceRoot, ParserConfiguration config) {
		final List<CompilationUnit> compilationUnits = new ArrayList<>();
		try {
			sourceRoot.parse("", config, (Path localPath, Path absolutePath, ParseResult<CompilationUnit> result) -> {
				if(result.getResult().isPresent()) {
					compilationUnits.add(result.getResult().get());
				}
				return Result.DONT_SAVE;
			});
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return compilationUnits;
	}
}
