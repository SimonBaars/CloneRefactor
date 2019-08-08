package com.simonbaars.clonerefactor.ast;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.utils.SourceRoot.Callback.Result;
import com.simonbaars.clonerefactor.SequenceObservable;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
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

public class CloneParser implements SetsIfNotNull, RemovesDuplicates, WritesErrors, CalculatesTimeIntervals, ResolvesSymbols {
	
	public DetectionResults parse(Path projectRoot, SourceRoot sourceRoot, ParserConfiguration config) {
		final List<CompilationUnit> compilationUnits = createAST(sourceRoot, config);
		
		if(compilationUnits.isEmpty())
			throw new IllegalStateException("Project has no sources!");
				
		DetectionResults d = null, prev = null;
		int oldRefactored, refactored = 0;
		do {
			oldRefactored = refactored;
			DetectionResults res = parseProject(projectRoot, sourceRoot, prev == null ? 0 : prev.getMetrics().generalStats.get("Generated Declarations"), compilationUnits);
			if(d == null)
				d = res;
			else prev.getMetrics().setChild(res.getMetrics());
			prev = res;
			refactored = res.getMetrics().generalStats.get("Amount Refactored");
		} while(oldRefactored != refactored);
		return d;
	}

	private DetectionResults parseProject(Path projectRoot, SourceRoot sourceRoot, int nGenerated, final List<CompilationUnit> compilationUnits) {
		final Map<String, ClassOrInterfaceDeclaration> classes = determineClasses(compilationUnits);
		ASTHolder.setClasses(classes);
		try {
			MetricCollector metricCollector = new MetricCollector(classes);
			long beginTime = System.currentTimeMillis();
			SequenceObservable seqObservable = new SequenceObservable().subscribe(new MetricObserver(metricCollector));
			Location lastLoc = calculateLineReg(metricCollector, compilationUnits, seqObservable);
			
			if(lastLoc!=null) {
				List<Sequence> findChains = new CloneDetection(seqObservable).findChains(lastLoc);
				doTypeSpecificTransformations(findChains);
				metricCollector.getMetrics().generalStats.increment("Detection time", interval(beginTime));
				DetectionResults res = new DetectionResults(metricCollector.reportClones(findChains), findChains);
				if(Settings.get().getRefactoringStrategy() != RefactoringStrategy.DONOTREFACTOR)
					new ExtractMethod(projectRoot, sourceRoot.getRoot(), compilationUnits, metricCollector, nGenerated, classes).refactor(findChains);
				return res;
			} else throw new IllegalStateException("Project has no usable sources!");
		} finally {
			ASTHolder.removeClasses();
		}
	}

	private Map<String, ClassOrInterfaceDeclaration> determineClasses(List<CompilationUnit> compilationUnits) {
		Map<String, ClassOrInterfaceDeclaration> classes = new HashMap<>();
		for(ClassOrInterfaceDeclaration classDecl : compilationUnits.stream().flatMap(cu -> cu.getTypes().stream()).filter(e -> e instanceof ClassOrInterfaceDeclaration).map(e -> (ClassOrInterfaceDeclaration)e).collect(Collectors.toList())) {
			resolve(classDecl::resolve).ifPresent(resolved -> classes.put(resolved.getQualifiedName(), classDecl));
		}
		return classes;
	}

	private void doTypeSpecificTransformations(List<Sequence> findChains) {
		doType2Transformations(findChains); 
		if (Settings.get().getCloneType() == CloneType.TYPE3R)
			new Type3Opportunities().determineType3Opportunities(findChains);
	}

	private void doType2Transformations(List<Sequence> findChains) {
		if(Settings.get().getCloneType().isNotType() && !Settings.get().isUseLiteratureTypeDefinitions()) {
			IntStream.range(0, findChains.size()).forEach(i -> {
				List<Sequence> determineVariability = new Type2Variability().determineVariability(findChains.remove(0));
				for(Sequence s : determineVariability) {
					if(removeDuplicatesOf(findChains, s) && s.size()>1)
						findChains.add(s);
				}
			});
		}
	}

	private final Location calculateLineReg(MetricCollector metricCollector, List<CompilationUnit> compilationUnits, SequenceObservable seqObservable) {
		NodeParser astParser = new NodeParser(metricCollector, seqObservable);
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
