package com.simonbaars.clonerefactor.refactoring;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.datatype.map.ListMap;
import com.simonbaars.clonerefactor.metrics.context.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.metrics.context.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.refactoring.target.ExtractToClassOrInterface;
import com.simonbaars.clonerefactor.refactoring.target.ExtractToNewInterface;
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
		MethodDeclaration decl = new MethodDeclaration(Modifier.createModifierList(Keyword.PRIVATE), getReturnType(s.getAny()), methodName);
		placeMethodOnBasisOfRelation(s, decl);
		removeLowestNodes(s, methodName);
		
		s.getAny().getContents().getNodes().forEach(node -> decl.getBody().get().addStatement((Statement)node));
		
		refactoredSequences.put(s, decl);
		writeRefactoringsToFile(s, decl);
	}

	private void placeMethodOnBasisOfRelation(Sequence s, MethodDeclaration decl) {
		RelationType relation = s.getRelationType();
		if(relation == RelationType.SAMECLASS || relation == RelationType.SAMEMETHOD) {
			new ExtractToClassOrInterface(s).extract(decl);
		} else if (relation == RelationType.UNRELATED) {
			new ExtractToNewInterface(s).extract(decl);
		}
	}

	private void writeRefactoringsToFile(Sequence s, MethodDeclaration decl) {
		for(Location p : getUniqueLocations(s.getLocations())) {
			try {
				FileUtils.writeStringToFile(SavePaths.createDirForFile(SavePaths.getRefactorFolder()+p.getFile().toString().replace(folder.getParent().toString(), "").substring(1)), getCompilationUnit(decl).toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
			MethodDeclaration d = getMethod(lastNode);
			if(d != null)
				return d.getType();
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
