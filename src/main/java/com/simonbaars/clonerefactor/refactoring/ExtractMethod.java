package com.simonbaars.clonerefactor.refactoring;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
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
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.datatype.map.ListMap;
import com.simonbaars.clonerefactor.metrics.context.enums.Refactorability;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.metrics.model.Relation;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.refactoring.target.ExtractToClassOrInterface;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.util.DoesFileOperations;
import com.simonbaars.clonerefactor.util.SavePaths;

public class ExtractMethod implements RequiresNodeContext, RequiresNodeOperations, DoesFileOperations {
	private final Map<Sequence, MethodDeclaration> refactoredSequences = new HashMap<>();
	private final GitChangeCommitter gitCommit;
	private final Path folder;
	private int x = 0;
	
	public ExtractMethod(Path path) {
		this.folder = path;
		gitCommit = Settings.get().getRefactoringStrategy().usesGit() ? new GitChangeCommitter(Paths.get(refactoringSaveFolder())) : new GitChangeCommitter();
	}
	
	public void tryToExtractMethod(Sequence s) {
		if(s.getRefactorability() == Refactorability.CANBEEXTRACTED) {
			if(Settings.get().getRefactoringStrategy().copyAll())
				copyFolder(folder, Paths.get(refactoringSaveFolder()));
			MethodDeclaration extractedMethod = extractMethod(s);
			if(gitCommit.doCommit())
				gitCommit.commit(s, extractedMethod);
		}
	}

	private MethodDeclaration extractMethod(Sequence s) {
		String methodName = "cloneRefactor"+(x++);
		MethodDeclaration decl = new MethodDeclaration(Modifier.createModifierList(), getReturnType(s.getAny()), methodName);
		placeMethodOnBasisOfRelation(s, decl);
		List<ExpressionStmt> methodcalls = removeLowestNodes(s, methodName);
		
		s.getAny().getContents().getNodes().forEach(node -> decl.getBody().get().addStatement((Statement)node));
		
		refactoredSequences.put(s, decl);
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
		String name = "CloneRefactor"+(x++);
		ClassOrInterfaceType implementedType = new JavaParser().parseClassOrInterfaceType(name).getResult().get();
		relation.getIntersectingClasses().forEach(c -> addType(c, createInterface).apply(implementedType));
		Optional<PackageDeclaration> pack = getCompilationUnit(s.getAny().getFirstNode()).get().getPackageDeclaration();
		CompilationUnit cu = pack.isPresent() ? new CompilationUnit(pack.get().getNameAsString()) : new CompilationUnit();
		relation.getIntersectingClasses().add(0, create(cu, createInterface).apply(name, createInterface ? new Keyword[] {Keyword.PUBLIC} : new Keyword[] {Keyword.PUBLIC, Keyword.ABSTRACT}));
	}
	
	public BiFunction<String, Keyword[], ClassOrInterfaceDeclaration> create(CompilationUnit cu, boolean createInterface) {
		return createInterface ? cu::addInterface : cu::addClass;
	}
	
	public Function<ClassOrInterfaceType, ClassOrInterfaceDeclaration> addType(ClassOrInterfaceDeclaration c, boolean createInterface){
		return createInterface ? c::addImplementedType : c::addExtendedType;
	}

	private void writeRefactoringsToFile(List<ExpressionStmt> methodcalls, Relation relation) {
		List<Node> saveNodes = new ArrayList<Node>(relation.getIntersectingClasses());
		saveNodes.addAll(methodcalls);
		try {
			for(CompilationUnit cu : getUniqueCompilationUnits(saveNodes))
				writeStringToFile(SavePaths.createDirForFile(compilationUnitFilePath(cu)), cu.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String compilationUnitFilePath(CompilationUnit unit) {
		return refactoringSaveFolder() + File.separator + packageToPath(unit) + getClassName(unit) + ".java";
	}
	
	private String refactoringSaveFolder() {
		return Settings.get().getRefactoringStrategy().originalLocation() ? folder.toString() : (SavePaths.getRefactorFolder() + folder.getFileName());
	}

	private String packageToPath(CompilationUnit unit) {
		return unit.getPackageDeclaration().isPresent() ? unit.getPackageDeclaration().get().getNameAsString().replace('.', File.separatorChar) : "";
	}

	private String getClassName(CompilationUnit unit) {
		return unit.getChildNodes().stream().filter(e -> e instanceof ClassOrInterfaceDeclaration).map(e -> (ClassOrInterfaceDeclaration)e).findAny().get().getNameAsString();
	}

	private List<ExpressionStmt> removeLowestNodes(Sequence s, String methodName) {
		ListMap<Location, Node> lowestNodes = new ListMap<>();
		Map<Location, BlockStmt> insideBlock = new HashMap<>();
		s.getLocations().forEach(e -> {
			List<Node> lowest = lowestNodes(e.getContents().getNodes());
			lowestNodes.put(e, lowest);
			if(lowest.get(0).getParentNode().isPresent() && lowest.get(0).getParentNode().get() instanceof BlockStmt)
				insideBlock.put(e, (BlockStmt)lowest.get(0).getParentNode().get());
		});
		if(lowestNodes.size() == insideBlock.size())
			return s.getLocations().stream().map(l -> removeLowestNodes(lowestNodes.get(l), insideBlock.get(l), methodName)).collect(Collectors.toList());
		return Collections.emptyList();
	}

	private ExpressionStmt removeLowestNodes(List<Node> lowestNodes, BlockStmt inBlock, String methodName) {
		ExpressionStmt methodcall = new ExpressionStmt(new MethodCallExpr(methodName));
		inBlock.getStatements().add(inBlock.getStatements().indexOf(lowestNodes.get(0)), methodcall);
		lowestNodes.forEach(inBlock::remove);
		return methodcall;
	}

	private Type getReturnType(Location any) {
		Node lastNode = any.getContents().getNodes().get(any.getContents().getNodes().size()-1);
		if(lastNode instanceof ReturnStmt) {
			Optional<MethodDeclaration> d = getMethod(lastNode);
			if(d.isPresent())
				return d.get().getType();
		}
		return new VoidType();
	}

	public void refactor(List<Sequence> findChains) {
		for(Sequence s : findChains) {
			if(noOverlap(refactoredSequences.keySet(), s)) {
				tryToExtractMethod(s);
			}
		}
	}

	private boolean noOverlap(Set<Sequence> keySet, Sequence s) {
		return keySet.stream().noneMatch(s::overlapsWith);
	}
}
