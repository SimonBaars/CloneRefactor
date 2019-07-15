package com.simonbaars.clonerefactor.refactoring;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.metrics.model.Relation;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.refactoring.target.ExtractToClassOrInterface;
import com.simonbaars.clonerefactor.util.FileUtils;
import com.simonbaars.clonerefactor.util.SavePaths;

public class ExtractMethod implements RequiresNodeContext, RequiresNodeOperations {
	private final Map<Sequence, MethodDeclaration> refactoredSequences = new HashMap<>();
	private final Path folder;
	private int x = 0;
	
	public ExtractMethod(Path path) {
		this.folder = path;
	}
	
	public void tryToExtractMethod(Sequence s) {
		if(s.getRefactorability() == Refactorability.CANBEEXTRACTED) {
			extractMethod(s);
		}
	}

	private void extractMethod(Sequence s) {
		String methodName = "cloneRefactor"+(x++);
		MethodDeclaration decl = new MethodDeclaration(Modifier.createModifierList(Keyword.FINAL), getReturnType(s.getAny()), methodName);
		placeMethodOnBasisOfRelation(s, decl);
		removeLowestNodes(s, methodName);
		
		s.getAny().getContents().getNodes().forEach(node -> decl.getBody().get().addStatement((Statement)node));
		
		refactoredSequences.put(s, decl);
		writeRefactoringsToFile(s, decl);
	}

	private void placeMethodOnBasisOfRelation(Sequence s, MethodDeclaration decl) {
		Relation relation = s.getRelation();
		if(relation.isEffectivelyUnrelated())
			createRelation(s, relation);
		new ExtractToClassOrInterface(relation.getIntersectingClass()).extract(decl);
		if(relation.getIntersectingClass().isInterface()) {
			decl.addModifier(Keyword.DEFAULT, Keyword.PUBLIC);
		} else if(relation.isSameClass()) {
			decl.addModifier(Keyword.PRIVATE);
		} else {
			decl.addModifier(Keyword.PROTECTED);
		}
	}

	private void createRelation(Sequence s, Relation relation) {
		Optional<PackageDeclaration> pack = getCompilationUnit(s.getAny().getAnyNode()).get().getPackageDeclaration();
		CompilationUnit cu = pack.isPresent() ? new CompilationUnit(pack.get().getNameAsString()) : new CompilationUnit();
		relation.setIntersectingClass(cu.addInterface("CloneRefactor"+(x++), Keyword.PUBLIC));
		Set<ClassOrInterfaceDeclaration> classOrInterface = s.getLocations().stream().map(l -> getClass(l.getAnyNode()).get()).collect(Collectors.toSet());
		ClassOrInterfaceType implementedType = new JavaParser().parseClassOrInterfaceType(relation.getIntersectingClass().getNameAsString()).getResult().get();
		classOrInterface.stream().filter(c -> c.getImplementedTypes().stream().noneMatch(t -> t.getNameAsString().equals(implementedType.getNameAsString()))).forEach(c -> c.addImplementedType(implementedType));
	}

	private void writeRefactoringsToFile(Sequence s, MethodDeclaration decl) {
		try {
			for(Location p : getUniqueLocations(s.getLocations()))
				FileUtils.writeStringToFile(SavePaths.createDirForFile(SavePaths.getRefactorFolder()+p.getFile().toString().replace(folder.getParent().toString(), "").substring(1)), getCompilationUnit(p.getAnyNode()).get().toString());
			CompilationUnit unit = getCompilationUnit(decl).get();
			FileUtils.writeStringToFile(new File(SavePaths.getRefactorFolder() + folder.getFileName() + File.separator + packageToPath(unit) + getClassName(unit) + ".java"), unit.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String packageToPath(CompilationUnit unit) {
		return unit.getPackageDeclaration().isPresent() ? unit.getPackageDeclaration().get().getNameAsString().replace('.', File.separatorChar) : "";
	}

	private String getClassName(CompilationUnit unit) {
		return unit.getChildNodes().stream().filter(e -> e instanceof ClassOrInterfaceDeclaration).map(e -> (ClassOrInterfaceDeclaration)e).findAny().get().getNameAsString();
	}

	private void removeLowestNodes(Sequence s, String methodName) {
		ListMap<Location, Node> lowestNodes = new ListMap<>();
		Map<Location, BlockStmt> insideBlock = new HashMap<>();
		s.getLocations().forEach(e -> {
			List<Node> lowest = lowestNodes(e.getContents().getNodes());
			lowestNodes.put(e, lowest);
			if(lowest.get(0).getParentNode().isPresent() && lowest.get(0).getParentNode().get() instanceof BlockStmt)
				insideBlock.put(e, (BlockStmt)lowest.get(0).getParentNode().get());
		});
		if(lowestNodes.size() == insideBlock.size())
			s.getLocations().forEach(l -> removeLowestNodes(lowestNodes.get(l), insideBlock.get(l), methodName));
	}

	private void removeLowestNodes(List<Node> lowestNodes, BlockStmt inBlock, String methodName) {
		inBlock.getStatements().add(inBlock.getStatements().indexOf(lowestNodes.get(0)), new ExpressionStmt(new MethodCallExpr(methodName)));
		lowestNodes.forEach(inBlock::remove);
	}

	private List<Location> getUniqueLocations(List<Location> locations) {
		Set<Path> set = new HashSet<>();
		List<Location> list = new ArrayList<>();
		for(Location location : locations) {
			if(!set.contains(location.getFile())) {
				list.add(location);
				set.add(location.getFile());
			}
		}
		return list;
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
