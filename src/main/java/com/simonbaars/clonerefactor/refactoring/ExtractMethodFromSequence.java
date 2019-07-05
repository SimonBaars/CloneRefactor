package com.simonbaars.clonerefactor.refactoring;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.utils.Pair;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.datatype.map.ListMap;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.metrics.enums.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.util.FileUtils;
import com.simonbaars.clonerefactor.util.SavePaths;

public class ExtractMethodFromSequence implements RequiresNodeContext, RequiresNodeOperations {
	private final Map<Sequence, MethodDeclaration> refactoredSequences = new HashMap<>();
	private final Path folder;
	private int x = 0;
	
	public ExtractMethodFromSequence(Path path) {
		this.folder = path;
	}
	
	public void tryToExtractMethod(Sequence s) {
		Refactorability ref = s.getRefactorability();
		if(ref == Refactorability.CANBEEXTRACTED) {
			RelationType relation = s.getRelationType();
			String methodName = "cloneRefactor"+(x++);
			MethodDeclaration decl = new MethodDeclaration(Modifier.createModifierList(Keyword.PRIVATE), getReturnType(s.getAny()), methodName);
			if(relation == RelationType.SAMECLASS || relation == RelationType.SAMEMETHOD) {
				ClassOrInterfaceDeclaration cd = getClass(s.getAny().getContents().getNodes().get(0));
				cd.getMembers().add(decl);
			}
			removeLowestNodes(s, methodName);
			
			s.getAny().getContents().getNodes().forEach(node -> decl.getBody().get().addStatement((Statement)node));
			
			refactoredSequences.put(s, decl);
			for(Location p : getUniqueLocations(s.getLocations())) {
				try {
					System.out.println("path = "+SavePaths.getRefactorFolder()+p.getFile().toString().replace(folder.getParent().toString(), "").substring(1));
					FileUtils.writeStringToFile(SavePaths.createDirForFile(SavePaths.getRefactorFolder()+p.getFile().toString().replace(folder.getParent().toString(), "").substring(1)), getCompilationUnit(decl).toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void removeLowestNodes(Sequence s, String methodName) {
		ListMap<Location, Node> lowestNodes = new ListMap<>();
		Map<Location, Pair<BlockStmt, Integer>> insideBlock = new HashMap<>();
		s.getLocations().forEach(e -> {
			List<Node> lowest = lowestNodes(e.getContents().getNodes());
			lowestNodes.put(e, lowest);
			if(lowest.get(0).getParentNode().isPresent() && lowest.get(0).getParentNode().get() instanceof BlockStmt) {
				BlockStmt blockStmt = (BlockStmt)lowest.get(0).getParentNode().get();
				insideBlock.put(e, new Pair<BlockStmt, Integer>(blockStmt, blockStmt.getStatements().indexOf(lowest.get(0))));
			}
		});
		if(lowestNodes.size() == insideBlock.size()) {
			s.getLocations().forEach(l -> {
				Pair<BlockStmt, Integer> block = insideBlock.get(l);
				removeLowestNodes(lowestNodes.get(l), block.a, block.b, methodName);
			});
		}
	}

	private void removeLowestNodes(List<Node> lowestNodes, BlockStmt inBlock, int index, String methodName) {
		System.out.println("Remove lowest");
		System.out.println(lowestNodes.stream().map(e -> e.getRange().get().toString()).collect(Collectors.joining(", ")));
		lowestNodes.forEach(n -> inBlock.remove(n));
		inBlock.getStatements().add(index, new ExpressionStmt(new MethodCallExpr(methodName)));
		//System.out.println(Arrays.toString(lowestNodes.toArray()));
		
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
		System.out.println("Refactor");
		for(Sequence s : findChains) {
			if(noOverlap(refactoredSequences.keySet(), s)) {
				System.out.println("refactoring "+s);
				tryToExtractMethod(s);
				System.out.println("Tried extract");
			}
		}
	}

	private boolean noOverlap(Set<Sequence> keySet, Sequence s) {
		return keySet.stream().noneMatch(s::overlapsWith);
	}
}
