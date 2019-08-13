package clonegraph;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.utils.SourceRoot.Callback.Result;
import com.google.common.cache.Cache;
import com.simonbaars.clonerefactor.context.MetricCollector;
import com.simonbaars.clonerefactor.detection.CloneDetection;
import com.simonbaars.clonerefactor.detection.interfaces.RemovesDuplicates;
import com.simonbaars.clonerefactor.detection.metrics.SequenceObservable;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.detection.type2.Type2Variability;
import com.simonbaars.clonerefactor.detection.type3.Type3Opportunities;
import com.simonbaars.clonerefactor.refactoring.ExtractMethod;
import com.simonbaars.clonerefactor.refactoring.enums.RefactoringStrategy;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.settings.progress.Progress;
import com.simonbaars.clonerefactor.thread.CalculatesTimeIntervals;
import com.simonbaars.clonerefactor.thread.WritesErrors;

import clonegraph.interfaces.ResolvesSymbols;
import clonegraph.interfaces.SetsIfNotNull;

public class CloneParser implements SetsIfNotNull, RemovesDuplicates, WritesErrors, CalculatesTimeIntervals, ResolvesSymbols {
	
	private final Path projectRoot;
	private final SourceRoot sourceRoot;
	private final ParserConfiguration config;

	public CloneParser(Path projectRoot, SourceRoot sourceRoot, ParserConfiguration config) {
		this.projectRoot = projectRoot;
		this.sourceRoot = sourceRoot;
		this.config = config;
	}
	
	public DetectionResults parse(JavaParserTypeSolver javaParserTypeSolver) {
		final Progress progress = new Progress(sourceRoot.getRoot());
		final List<CompilationUnit> compilationUnits = createAST(progress, javaParserTypeSolver);
		return parseProject(compilationUnits, progress);
	}

	private DetectionResults parseProject(final List<CompilationUnit> compilationUnits, Progress progress) {
		try {
			ASTHolder.setClasses(determineClasses(compilationUnits));
			progress.nextStage(compilationUnits.size());
			MetricCollector metricCollector = new MetricCollector();
			long beginTime = System.currentTimeMillis();
			SequenceObservable seqObservable = new SequenceObservable().subscribe(new MetricObserver(metricCollector));
			Location lastLoc = calculateLineReg(metricCollector, compilationUnits, seqObservable, progress);
			if(lastLoc!=null) {
				progress.nextStage(metricCollector.getMetrics().generalStats.get("Total Nodes"));
				List<Sequence> findChains = new CloneDetection(seqObservable).findChains(lastLoc, progress);
				doTypeSpecificTransformations(findChains);
				metricCollector.getMetrics().generalStats.increment("Detection time", interval(beginTime));
				progress.nextStage(findChains.size());
				DetectionResults res = new DetectionResults(metricCollector.reportClones(findChains, progress), findChains);
				if(Settings.get().getRefactoringStrategy() != RefactoringStrategy.DONOTREFACTOR) {
					progress.nextStage();
					ExtractMethod extractMethod = new ExtractMethod(projectRoot, sourceRoot.getRoot(), compilationUnits, metricCollector);
					extractMethod.refactor(findChains, progress);
					res.getRefactorResults().addAll(extractMethod.getRes());
					metricCollector.resetMetrics();
					res.getMetrics().setChild(metricCollector.reportClones(findChains, progress));
				}
				return res;
			} else throw new IllegalStateException("Project has no usable sources!");
		} finally {
			ASTHolder.removeClasses();
		}
	}

	private List<CompilationUnit> createAST(Progress progress, JavaParserTypeSolver javaParserTypeSolver) {
		final List<CompilationUnit> compilationUnits = parseAST(progress, javaParserTypeSolver);
		
		if(compilationUnits.isEmpty())
			throw new IllegalStateException("Project has no sources! "+sourceRoot.getRoot()+", "+projectRoot);

		return compilationUnits;
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
		if (Settings.get().getCloneType().isType3())
			new Type3Opportunities().determineType3Opportunities(findChains);
	}

	private void doType2Transformations(List<Sequence> findChains) {
		if(Settings.get().getCloneType().isNotType1() && !Settings.get().useLiteratureTypeDefinitions()) {
			IntStream.range(0, findChains.size()).forEach(i -> {
				List<Sequence> determineVariability = new Type2Variability().determineVariability(findChains.remove(0));
				for(Sequence s : determineVariability) {
					if(removeDuplicatesOf(findChains, s) && s.size()>1)
						findChains.add(s);
				}
			});
		}
	}

	private final Location calculateLineReg(MetricCollector metricCollector, List<CompilationUnit> compilationUnits, SequenceObservable seqObservable, Progress progress) {
		NodeParser astParser = new NodeParser(metricCollector, seqObservable);
		Location l = null;
		for(CompilationUnit cu : compilationUnits) {
			l = astParser.extractLinesFromAST(l, cu, cu);
			progress.next();
		}
		return l;
	}

	private List<CompilationUnit> parseAST(Progress progress, JavaParserTypeSolver javaParserTypeSolver) {
		final List<CompilationUnit> compilationUnits = new ArrayList<>();
		try {
			sourceRoot.parse("", config, (Path localPath, Path absolutePath, ParseResult<CompilationUnit> result) -> {
				if(result.getResult().isPresent()) {
					compilationUnits.add(result.getResult().get());
					setupTypeSolver(absolutePath, javaParserTypeSolver, result.getResult().get());
				}
				progress.next();
				return Result.DONT_SAVE;
			});
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return compilationUnits;
	}

	@SuppressWarnings("unchecked")
	private void setupTypeSolver(Path path, JavaParserTypeSolver javaParserTypeSolver, CompilationUnit compilationUnit) {
		try {
			Field f = javaParserTypeSolver.getClass().getDeclaredField("parsedFiles");
			f.setAccessible(true);
			Cache<Path, Optional<CompilationUnit>> iWantThis = (Cache<Path, Optional<CompilationUnit>>) f.get(javaParserTypeSolver);
			iWantThis.put(path, Optional.of(compilationUnit));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //NoSuchFieldException
		
	}
}
