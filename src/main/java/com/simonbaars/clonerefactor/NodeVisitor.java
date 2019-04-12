package com.simonbaars.clonerefactor;

import java.util.function.Function;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.model.Location;

public class NodeVisitor {
	
	public void visitNode(Node n) {
		if(n instanceof ImportDeclaration)
			return;
		
		n.getChildNodes().forEach(e -> visitNode(e));
	}

	public Location visitNode(Node n, Function<Node, Location> c) {
		if(n instanceof ImportDeclaration)
			return null;
		
		Location l = c.apply(n);
		
		for(Node n : n.getChildNodes())
		n.getChildNodes().forEach(e -> l = visitNode(e, c));
	}
}
