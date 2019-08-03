package com.simonbaars.clonerefactor.refactoring;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.github.javaparser.JavaParser;
import com.github.javaparser.JavaToken;
import com.github.javaparser.JavaToken.Category;
import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.datatype.map.ListMap;
import com.simonbaars.clonerefactor.metrics.MetricCollector;
import com.simonbaars.clonerefactor.metrics.context.enums.Refactorability;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.metrics.model.Relation;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.refactoring.model.PostMetrics;
import com.simonbaars.clonerefactor.refactoring.model.PreMetrics;
import com.simonbaars.clonerefactor.refactoring.populate.PopulateArguments;
import com.simonbaars.clonerefactor.refactoring.populate.PopulateReturnValue;
import com.simonbaars.clonerefactor.refactoring.populate.PopulateThrows;
import com.simonbaars.clonerefactor.refactoring.populate.PopulatesExtractedMethod;
import com.simonbaars.clonerefactor.refactoring.target.ExtractToClassOrInterface;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.util.DoesFileOperations;
import com.simonbaars.clonerefactor.util.SavePaths;

public class ExtractMethod implements RequiresNodeContext, RequiresNodeOperations, DoesFileOperations {
	private final PopulatesExtractedMethod[] populators = {new PopulateThrows(), new PopulateArguments(), new PopulateReturnValue()};
	private static final String METHOD_NAME = "cloneRefactor";
	
	private final Map<Sequence, MethodDeclaration> refactoredSequences = new HashMap<>();
	private final Set<File> formatted = new HashSet<>();
	private final GitChangeCommitter gitCommit;
	
	private final Path projectFolder;
	private final Path sourceFolder;
	
	private int nGeneratedDeclarations = 0;
	private final MetricCollector metricCollector;

	private final List<CompilationUnit> compilationUnits;
	
	public ExtractMethod(Path projectPath, Path sourceFolder) {
		this(projectPath, sourceFolder, new ArrayList<>(), null);
	}
	
	public ExtractMethod(Path projectRoot, Path root, List<CompilationUnit> compilationUnits, MetricCollector metricCollector) {
		this.projectFolder = projectRoot;
		this.sourceFolder = root;
		Path saveFolder = Paths.get(refactoringSaveFolder(false));
		if(Settings.get().getRefactoringStrategy().copyAll())
			copyFolder(projectFolder, saveFolder);
		gitCommit = Settings.get().getRefactoringStrategy().usesGit() ? new GitChangeCommitter(saveFolder) : new GitChangeCommitter();
		this.compilationUnits = compilationUnits;
		this.metricCollector = metricCollector;
	}

	public void tryToExtractMethod(Sequence s) {
		if(s.getRefactorability() == Refactorability.CANBEEXTRACTED) {
			if(s.getRelation().isEffectivelyUnrelated() && metricCollector != null)
				metricCollector.reassessRelation(s);
			MethodDeclaration extractedMethod = extractMethod(s);
			if(gitCommit.doCommit())
				gitCommit.commit(s, extractedMethod);
		}
	}

	private MethodDeclaration extractMethod(Sequence s) {
		String methodName = METHOD_NAME+(nGeneratedDeclarations++);
		MethodDeclaration decl = new MethodDeclaration(Modifier.createModifierList(), new VoidType(), methodName);
		placeMethodOnBasisOfRelation(s, decl);
		List<Statement> methodcalls = removeLowestNodes(s, decl);
		Arrays.stream(populators).forEach(p -> p.postPopulate(decl));
		refactoredSequences.put(s, decl);
		new PostMetrics(decl, s.getRelation().isEffectivelyUnrelated() ? getClass(decl) : Optional.empty(), methodcalls).combine(new PreMetrics(s)).save(metricCollector);
		if(Settings.get().getRefactoringStrategy().savesFiles())
			writeRefactoringsToFile(methodcalls, s.getRelation());
		return decl;
	}

	private void placeMethodOnBasisOfRelation(Sequence s, MethodDeclaration decl) {
		Relation relation = s.getRelation();
		if(relation.isEffectivelyUnrelated())
			createRelation(s, relation);
		new ExtractToClassOrInterface(relation.getFirstClass()).extract(decl);
		addKeywords(decl, relation);
	}

	private void addKeywords(MethodDeclaration decl, Relation relation) {
		if(relation.isInterfaceRelation()) {
			decl.addModifier(Keyword.PUBLIC, Keyword.DEFAULT);
		} else if(relation.isSameClass()) {
			decl.addModifier(Keyword.PRIVATE);
		} else {
			decl.addModifier(Keyword.PROTECTED);
		}
	}

	private void createRelation(Sequence s, Relation relation) {
		boolean createInterface = relation.isInterfaceRelation();
		String name = "CloneRefactor"+(nGeneratedDeclarations++);
		ClassOrInterfaceType implementedType = new JavaParser().parseClassOrInterfaceType(name).getResult().get();
		relation.getIntersectingClasses().forEach(c -> addType(c, createInterface).apply(implementedType));
		Optional<PackageDeclaration> pack = getCompilationUnit(s.getAny().getFirstNode()).get().getPackageDeclaration();
		CompilationUnit cu = pack.isPresent() ? new CompilationUnit(pack.get().getNameAsString()) : new CompilationUnit();
		compilationUnits.add(cu);
		relation.getIntersectingClasses().add(0, create(cu, createInterface).apply(name, createInterface ? new Keyword[] {Keyword.PUBLIC} : new Keyword[] {Keyword.PUBLIC, Keyword.ABSTRACT}));
		if(metricCollector!=null) metricCollector.reportClass(relation.getFirstClass());
	}
	
	public BiFunction<String, Keyword[], ClassOrInterfaceDeclaration> create(CompilationUnit cu, boolean createInterface) {
		return createInterface ? cu::addInterface : cu::addClass;
	}
	
	public Function<ClassOrInterfaceType, ClassOrInterfaceDeclaration> addType(ClassOrInterfaceDeclaration c, boolean createInterface){
		return createInterface ? c::addImplementedType : c::addExtendedType;
	}

	private void writeRefactoringsToFile(List<Statement> methodcalls, Relation relation) {
		List<Node> saveNodes = new ArrayList<>(relation.getIntersectingClasses());
		saveNodes.addAll(methodcalls);
		try {
			for(CompilationUnit cu : getUniqueCompilationUnits(saveNodes))
				save(cu);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void save(CompilationUnit cu) throws IOException {
		File file = SavePaths.createDirForFile(compilationUnitFilePath(cu));
		writeStringToFile(file, cu.toString());
	}

	private String compilationUnitFilePath(CompilationUnit unit) {
		return refactoringSaveFolder(true) + File.separator + packageToPath(unit) + File.separator + getClassName(unit) + ".java";
	}
	
	private String refactoringSaveFolder(boolean sources) {
		if(Settings.get().getRefactoringStrategy().originalLocation())
			return (sources ? sourceFolder : projectFolder).toString();
		return SavePaths.getRefactorFolder(sources ? outputSourceFolder() : "");
	}
	
	private String outputSourceFolder() {
		return  sourceFolder.toString().replace(projectFolder.toString(), "");
	}

	private String packageToPath(CompilationUnit unit) {
		return unit.getPackageDeclaration().isPresent() ? unit.getPackageDeclaration().get().getNameAsString().replace('.', File.separatorChar) : "";
	}

	private String getClassName(CompilationUnit unit) {
		return unit.getChildNodes().stream().filter(e -> e instanceof ClassOrInterfaceDeclaration).map(e -> (ClassOrInterfaceDeclaration)e).findAny().get().getNameAsString();
	}

	private List<Statement> removeLowestNodes(Sequence s, MethodDeclaration decl) {
		ListMap<Location, Node> lowestNodes = new ListMap<>();
		Map<Location, BlockStmt> insideBlock = new HashMap<>();
		s.getLocations().forEach(e -> {
			List<Node> lowest = lowestNodes(e.getContents().getNodes());
			lowestNodes.put(e, lowest);
			if(lowest.get(0).getParentNode().isPresent() && lowest.get(0).getParentNode().get() instanceof BlockStmt)
				insideBlock.put(e, (BlockStmt)lowest.get(0).getParentNode().get());
		});
		Arrays.stream(populators).forEach(p -> p.prePopulate(decl, lowestNodes.get(s.getAny())));
		lowestNodes.get(s.getAny()).forEach(node -> decl.getBody().get().addStatement((Statement)node));
		if(lowestNodes.size() == insideBlock.size())
			return s.getLocations().stream().map(l -> 
				removeLowestNodes(lowestNodes.get(l), insideBlock.get(l), decl)
			).collect(Collectors.toList());
		return Collections.emptyList();
	}

	private Statement removeLowestNodes(List<Node> lowestNodes, BlockStmt inBlock, MethodDeclaration decl) {
		MethodCallExpr methodCallExpr = new MethodCallExpr(decl.getNameAsString());
		Statement methodCallStmt = Arrays.stream(populators).map(p -> p.modifyMethodCall(methodCallExpr)).filter(Optional::isPresent).map(Optional::get).findAny().orElse(new ExpressionStmt(methodCallExpr));
		if(Settings.get().getRefactoringStrategy().savesFiles())
			saveASTBeforeChange(getCompilationUnit(inBlock).get());
		inBlock.getStatements().add(inBlock.getStatements().indexOf(lowestNodes.get(0)), methodCallStmt);
		lowestNodes.forEach(inBlock::remove);
		return methodCallStmt;
	}

	private void saveASTBeforeChange(CompilationUnit cu) {
		File file = SavePaths.createDirForFile(compilationUnitFilePath(cu));
		if(formatted.add(file) && file.exists()) {
			try {
				writeStringToFile(file, cu.toString());
				gitCommit.commitFormat(getClassName(cu));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int refactor(List<Sequence> foundCloneClasses) {
		for(Sequence s : foundCloneClasses) {
			if(noOverlap(refactoredSequences.keySet(), s) && !isGenerated(s)) {
				tryToExtractMethod(s);
			}
		}
		return nGeneratedDeclarations;
	}

	private boolean isGenerated(Sequence s) {
		return s.getNodeSize() == 1 && s.getLocations().stream().allMatch(l -> l.getContents().getTokens().stream().anyMatch(t -> t.asString().equals(METHOD_NAME)));
	}

	private boolean noOverlap(Set<Sequence> keySet, Sequence s) {
		return keySet.stream().noneMatch(s::overlapsWith);
	}
	
	public void determineRanges(CompilationUnit cu) {
		determineRanges(cu, new Position(1, 1));
	}
	
	private Position determineRanges(Node node, Position currentPosition) {
		Optional<TokenRange> tr = node.getTokenRange();
		if(!tr.isPresent() || currentPosition == null)
			return null;
		Position end = determineTokenRanges(currentPosition, tr.get());
		node.setRange(new Range(currentPosition, end));
		for(Node childNode : node.getChildNodes()) {
			currentPosition = determineRanges(childNode, determineStartPosition(node, tr.get(), currentPosition));
		}
		return currentPosition;
	}

	private Position determineStartPosition(Node node, TokenRange tokenRange, Position currentPosition) {
		Optional<TokenRange> tr = node.getTokenRange();
		if(!tr.isPresent())
			return null;
		JavaToken firstToken = tr.get().getBegin();
		return StreamSupport.stream(tr.get().spliterator(), false)
				.filter(token -> token == firstToken).map(token -> token.getRange())
				.filter(e -> e.isPresent()).map(e -> e.get().begin).findAny().orElse(null);
	}

	private Position determineTokenRanges(Position currentPosition, TokenRange tokenRange) {
		for(JavaToken token : tokenRange) {
			Position end = token.getCategory() == Category.EOL ? 
					new Position(currentPosition.line + 1, 1) :
					new Position(currentPosition.line, currentPosition.column + token.toString().length());
			token.setRange(new Range(currentPosition, end));
			currentPosition = end;
		}
		return currentPosition;
	}
}
