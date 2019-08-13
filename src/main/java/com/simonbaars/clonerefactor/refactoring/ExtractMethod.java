package com.simonbaars.clonerefactor.refactoring;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.CompilationUnit.Storage;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithStatements;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;
import com.simonbaars.clonerefactor.ast.ASTHolder;
import com.simonbaars.clonerefactor.ast.MetricObserver;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.datatype.map.CountMap;
import com.simonbaars.clonerefactor.datatype.map.ListMap;
import com.simonbaars.clonerefactor.datatype.map.SimpleTable;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.metrics.MetricCollector;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.metrics.context.enums.Refactorability;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.metrics.model.Relation;
import com.simonbaars.clonerefactor.refactoring.enums.MethodType;
import com.simonbaars.clonerefactor.refactoring.model.CombinedMetrics;
import com.simonbaars.clonerefactor.refactoring.model.PostMetrics;
import com.simonbaars.clonerefactor.refactoring.model.PreMetrics;
import com.simonbaars.clonerefactor.refactoring.populate.PopulateArguments;
import com.simonbaars.clonerefactor.refactoring.populate.PopulateReturnValue;
import com.simonbaars.clonerefactor.refactoring.populate.PopulateReturningFlow;
import com.simonbaars.clonerefactor.refactoring.populate.PopulateThrows;
import com.simonbaars.clonerefactor.refactoring.populate.PopulatesExtractedMethod;
import com.simonbaars.clonerefactor.refactoring.target.ExtractToClassOrInterface;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.settings.progress.Progress;
import com.simonbaars.clonerefactor.util.DoesFileOperations;
import com.simonbaars.clonerefactor.util.SavePaths;

public class ExtractMethod implements RequiresNodeContext, RequiresNodeOperations, DoesFileOperations, ResolvesSymbols {
	private final PopulatesExtractedMethod[] populators = {new PopulateThrows(), new PopulateArguments(), new PopulateReturnValue(), new PopulateReturningFlow()};
	private static final String METHOD_NAME = "cloneRefactor";
	
	private final Map<Sequence, MethodDeclaration> refactoredSequences = new HashMap<>();
	private final Set<File> formatted = new HashSet<>();
	private final GitChangeCommitter gitCommit;
	
	private final Path projectFolder;
	private final Path sourceFolder;
	
	private int gen = 1;
	private final MetricCollector metricCollector;

	private final List<CompilationUnit> compilationUnits;
	private final CountMap<String> metrics = new CountMap<>();
	private final SimpleTable res = new SimpleTable();
	
	private final Set<Storage> modified = new HashSet<>();
	
	public ExtractMethod(Path projectRoot, Path root, List<CompilationUnit> compilationUnits, MetricCollector metricCollector) {
		this.projectFolder = projectRoot;
		this.sourceFolder = root; 
		Path saveFolder = Paths.get(refactoringSaveFolder(false));
		if(Settings.get().getRefactoringStrategy().copyAll() && !saveFolder.toFile().exists())
			copyFolder(projectFolder, saveFolder);
		gitCommit = Settings.get().getRefactoringStrategy().usesGit() ? new GitChangeCommitter(saveFolder) : new GitChangeCommitter();
		this.compilationUnits = compilationUnits;
		this.metricCollector = metricCollector;

		metrics.put(MetricObserver.metricTotalSize(ProblemType.UNITCOMPLEXITY), metricCollector.getMetrics().generalStats.get(MetricObserver.metricTotalSize(ProblemType.UNITCOMPLEXITY)));
		metrics.put(MetricObserver.metricTotalSize(ProblemType.UNITINTERFACESIZE), metricCollector.getMetrics().generalStats.get(MetricObserver.metricTotalSize(ProblemType.UNITINTERFACESIZE)));
		metrics.put(MetricObserver.metricTotalSize(ProblemType.LINEVOLUME), metricCollector.getMetrics().generalStats.get(MetricObserver.metricTotalSize(ProblemType.LINEVOLUME)));
		metrics.put(MetricObserver.metricTotalSize(ProblemType.TOKENVOLUME), metricCollector.getMetrics().generalStats.get(MetricObserver.metricTotalSize(ProblemType.TOKENVOLUME)));
		metrics.put("Total Nodes", metricCollector.getMetrics().generalStats.get("Total Nodes"));
		metrics.put("Cloned Nodes", metricCollector.getMetrics().generalStats.get("Cloned Nodes"));
		metrics.put("Cloned Tokens", metricCollector.getMetrics().generalStats.get("Cloned Tokens"));
		metrics.put("Cloned Lines", metricCollector.getMetrics().generalStats.get("Cloned Lines"));
	}

	public void tryToExtractMethod(Sequence s) {
		if(s.getRefactorability() == Refactorability.CANBEEXTRACTED && noOverlap(refactoredSequences.keySet(), s)) {
			if(s.getRelation().isEffectivelyUnrelated())
				metricCollector.reassessRelation(s);
			String extractedMethod = extractMethod(s);
			metricCollector.getMetrics().generalStats.increment("Amount Refactored");
			if(gitCommit.doCommit())
				gitCommit.commit(extractedMethod);
		}
	}

	private String extractMethod(Sequence s) {
		String methodName = METHOD_NAME+(gen++);
		PreMetrics preMetrics = new PreMetrics(s);
		MethodDeclaration decl = new MethodDeclaration(Modifier.createModifierList(), new VoidType(), methodName);
		placeMethodOnBasisOfRelation(s, decl);
		List<Statement> methodcalls = removeLowestNodes(s, decl);;
		if(getClass(decl).get().isInterface() && !s.getRelation().isInterfaceRelation())
			decl.setModifiers(Keyword.PUBLIC, Keyword.DEFAULT);
		Arrays.stream(populators).forEach(p -> p.postPopulate(s, decl));
		refactoredSequences.put(s, decl);
		Optional<ClassOrInterfaceDeclaration> cu = s.getRelation().isEffectivelyUnrelated() ? getClass(decl) : Optional.empty();
		if(cu.isPresent()) compilationUnits.add(makeValidAfterChanges(getCompilationUnit(cu.get()).get()));
		CombinedMetrics combine = new PostMetrics(decl, cu, methodcalls).combine(preMetrics);
		combine.saveTable(res, s, projectFolder.getFileName().toString(), decl, calculateMethodType(methodcalls.get(0)));
		storeChanges(s, decl, methodcalls);
		return generateDescription(s, decl) + combine.save(metricCollector, metrics);
	}
	
	private MethodType calculateMethodType(Statement statement) {
		if(statement instanceof ReturnStmt)
			return MethodType.RETURNS;
		ExpressionStmt exprStmt = (ExpressionStmt)statement;
		if(exprStmt.getExpression() instanceof VariableDeclarationExpr)
			return MethodType.RETURNSDECLAREDVARIABLE;
		else if(exprStmt.getExpression() instanceof AssignExpr)
			return MethodType.RETURNSASSIGNEDVARIABLE;
		return MethodType.VOID;
	}

	private String generateDescription(Sequence s, MethodDeclaration extractedMethod) {
		StringBuilder b = new StringBuilder("Created unified method in "+s.getRelation().getFirstClass().getNameAsString()+"\n\nCloneRefactor refactored a clone class with "+s.size()+" clone instances. For the common code we created a new method and named this method \""+extractedMethod.getNameAsString()+"\". These clone instances have an "+s.getRelation().getType()+" relation with each other. ");
		if(s.getRelation().isEffectivelyUnrelated()) {
			b.append("Because there is no location we could place the generated method, as at least one clone instance is unrelated with the rest, we created a new "+whatIsIt(s.getRelation().getFirstClass())+". We named this "+whatIsIt(s.getRelation().getFirstClass()));
		} else {
			b.append("The newly created method has been placed in");
		}
		b.append(" "+s.getRelation().getFirstClass().getNameAsString()+". Each duplicated fragment has been replaced with a call to this method.\n\n");
		return b.toString();
	}
	
	private String whatIsIt(ClassOrInterfaceDeclaration firstClass) {
		return firstClass.isInterface() ? "interface" : "class";
	}

	private void storeChanges(Sequence s, MethodDeclaration decl, List<Statement> methodcalls) {
		List<Node> saveNodes = new ArrayList<>(s.getRelation().getIntersectingClasses());
		saveNodes.addAll(methodcalls);
		Collection<CompilationUnit> cus = getUniqueCompilationUnits(saveNodes);
		cus.forEach(cu -> modified.add(cu.getStorage().get()));
		if(Settings.get().getRefactoringStrategy().savesFiles())
			cus.forEach(this::save);
	}

	private void placeMethodOnBasisOfRelation(Sequence s, MethodDeclaration decl) {
		Relation relation = s.getRelation();
		if(Settings.get().getRefactoringStrategy().usesGit())
			relation.getIntersectingClasses().forEach(c -> saveASTBeforeChange(getCompilationUnit(c).get()));
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
		String name = "CloneRefactor"+(gen++);
		ClassOrInterfaceType implementedType = new JavaParser().parseClassOrInterfaceType(name).getResult().get();
		addTypesToIntersectingClasses(relation, createInterface, implementedType);
		Optional<PackageDeclaration> pack = getCompilationUnit(s.getAny().getFirstNode()).get().getPackageDeclaration();
		CompilationUnit cu = pack.isPresent() ? new CompilationUnit(pack.get().getNameAsString()) : new CompilationUnit();
		relation.getIntersectingClasses().add(0, create(cu, createInterface).apply(name, createInterface ? new Keyword[] {Keyword.PUBLIC} : new Keyword[] {Keyword.PUBLIC, Keyword.ABSTRACT}));
		cu.setStorage(Paths.get(compilationUnitFilePath(cu)));
		resolve(relation.getFirstClass()::resolve).ifPresent(type -> ASTHolder.getClasses().put(type.getQualifiedName(), relation.getFirstClass()));
	}

	private void addTypesToIntersectingClasses(Relation relation, boolean createInterface, ClassOrInterfaceType implementedType) {
		relation.getIntersectingClasses().forEach(c -> {
			addType(c, createInterface).apply(implementedType);
			if(!createInterface && c.getExtendedTypes().size() == 2)
				c.getExtendedTypes().remove(0); // First extended type is Object
		});
	}
	
	public BiFunction<String, Keyword[], ClassOrInterfaceDeclaration> create(CompilationUnit cu, boolean createInterface) {
		return createInterface ? cu::addInterface : cu::addClass;
	}
	
	public Function<ClassOrInterfaceType, ClassOrInterfaceDeclaration> addType(ClassOrInterfaceDeclaration c, boolean createInterface){
		return createInterface && !c.isInterface() ? c::addImplementedType : c::addExtendedType;
	}

	private void save(CompilationUnit cu) {
		File file = SavePaths.createDirForFile(compilationUnitFilePath(cu));
		try {
			writeStringToFile(file, cu.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String compilationUnitFilePath(CompilationUnit unit) {
		return refactoringSaveFolder(true) + File.separator + packageToPath(unit) + File.separator + getClassName(unit) + ".java";
	}
	
	private String refactoringSaveFolder(boolean sources) {
		if(Settings.get().getRefactoringStrategy().originalLocation())
			return (sources ? sourceFolder : projectFolder).toString();
		return SavePaths.getRefactorFolder(projectFolder.getFileName().toString(), sources ? outputSourceFolder() : "");
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
		s.getLocations().forEach(e -> lowestNodes.put(e, lowestNodes(e.getContents().getNodes())));
		Arrays.stream(populators).forEach(p -> p.prePopulate(decl, lowestNodes.get(s.getAny())));
		List<Statement> methodcalls = s.getLocations().stream().map(l -> removeLowestNodes(s, lowestNodes.get(l), decl.getNameAsString())).collect(Collectors.toList());
		lowestNodes.get(s.getAny()).forEach(node -> decl.getBody().get().addStatement((Statement)node));
		return methodcalls;
	}

	private Statement removeLowestNodes(Sequence s, List<Node> lowestNodes, String name) {
		MethodCallExpr methodCallExpr = new MethodCallExpr(name);
		Statement methodCallStmt = Arrays.stream(populators).map(p -> p.modifyMethodCall(s, methodCallExpr)).filter(Optional::isPresent).map(Optional::get).findAny().orElse(new ExpressionStmt(methodCallExpr));
		if(Settings.get().getRefactoringStrategy().usesGit())
			saveASTBeforeChange(getCompilationUnit(lowestNodes.get(0)).get());
		placeMethodCall(lowestNodes, methodCallStmt);
		return methodCallStmt;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void placeMethodCall(List<Node> lowestNodes, Statement methodCallStmt) {
		Node parent = lowestNodes.get(0).getParentNode().get();
		if(parent instanceof NodeWithStatements) {
			NodeWithStatements inBlock = ((NodeWithStatements)parent);
			inBlock.getStatements().add(inBlock.getStatements().indexOf(lowestNodes.get(0)), methodCallStmt);
			lowestNodes.forEach(inBlock.getStatements()::remove);
		} else if(parent instanceof IfStmt) {
			IfStmt inBlock = ((IfStmt)parent);
			if(inBlock.getThenStmt() == lowestNodes.get(0)) {
				inBlock.setThenStmt(methodCallStmt);
			} else {
				inBlock.setElseStmt(methodCallStmt);
			}	
		} else if(parent instanceof WhileStmt) {
			((WhileStmt)parent).setBody(methodCallStmt);
		} else throw new IllegalStateException("Could not place a method call! Parent node "+parent.getClass()+".");
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
	
	private boolean noOverlap(Set<Sequence> keySet, Sequence s) {
		return keySet.stream().noneMatch(s::overlapsWith);
	}

	public void refactor(List<Sequence> foundCloneClasses, Progress progress) {
		Collections.sort(foundCloneClasses);
		for(int i = 0; i<foundCloneClasses.size(); i++) {
			tryToExtractMethod(foundCloneClasses.get(i));
			progress.next();
			foundCloneClasses.remove(i);
		}
		metricCollector.getMetrics().generalStats.put("Generated Declarations", gen);
		fixModified();
	}

	private void fixModified() {
		for(int i = 0; i<compilationUnits.size(); i++) {
			if(modified.contains(compilationUnits.get(i).getStorage().get())) {
				compilationUnits.set(i, makeValidAfterChanges(compilationUnits.get(i)));
			}
		}
		modified.clear();
	}
	
	public CompilationUnit makeValidAfterChanges(CompilationUnit cu) {
		ParseResult<CompilationUnit> pr = new JavaParser().parse(cu.toString());
		if(pr.isSuccessful()) {
			pr.getResult().get().setStorage(cu.getStorage().get().getPath());
			cu = pr.getResult().get();
		} else throw new IllegalStateException("Failed to parse "+cu+": "+Arrays.toString(pr.getProblems().toArray()));
		return cu;
	}

	public SimpleTable getRes() {
		return res;
	}
}
