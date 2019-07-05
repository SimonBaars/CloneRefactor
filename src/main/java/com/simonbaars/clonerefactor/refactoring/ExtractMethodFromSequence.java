package com.simonbaars.clonerefactor.refactoring;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
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
			ListMap<Location, Node> lowestNodes = new ListMap<>();
			Map<Location, BlockStmt> insideBlock = new HashMap<>();
			s.getLocations().forEach(e -> {
				List<Node> lowest = lowestNodes(e.getContents().getNodes());
				lowestNodes.put(e, lowest);
				if(lowest.get(0).getParentNode().isPresent() && lowest.get(0).getParentNode().get() instanceof BlockStmt) insideBlock.put(e, (BlockStmt)lowest.get(0).getParentNode().get());
			});
			s.getLocations().forEach(e -> insideBlock.put(e, lowestNodes.get(e).));
			List<Node> lowestNodes = lowestNodes(s.getAny().getContents().getNodes());
			List<BlockStmt> removeFromMethod = lowestNodes.stream().map(e -> e.getParentNode()).map(e -> e.isPresent() && e.get() instanceof BlockStmt ?  (BlockStmt)e.get() : null).collect(Collectors.toList());
			s.getLocations().forEach(l -> removeLowestNodes(l, methodName));
			s.getAny().getContents().getNodes().forEach(node -> decl.getBody().get().addStatement((Statement)node));
			
			refactoredSequences.put(s, decl);
			for(Location p : getUniqueLocations(s.getLocations())) {
				try {
					//System.out.println("path = "+p.getFile()+", folder = "+folder+", relative = "+p.getFile().relativize(folder.getParent()));
					FileUtils.writeStringToFile(SavePaths.createDirForFile(SavePaths.getRefactorFolder()+p.getFile().relativize(folder.getParent()).toString()), getCompilationUnit(p.getContents().getNodes().get(0)).toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void removeLowestNodes(Location l, String methodName) {
		System.out.println("Remove lowest");
		Collections.reverse(lowestNodes);
		System.out.println(lowestNodes.stream().map(e -> e.getRange().get().toString()).collect(Collectors.joining(", ")));
		Node parent = lowestNodes.get(0).getParentNode().get();
		if(parent instanceof BlockStmt) {
			int firstIndex = ((BlockStmt)parent).getStatements().indexOf(lowestNodes.get(0));
			lowestNodes.forEach(n -> n.getParentNode().get().remove(n));
			((BlockStmt)parent).getStatements().add(firstIndex, new ExpressionStmt(new MethodCallExpr(methodName)));
		}
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
