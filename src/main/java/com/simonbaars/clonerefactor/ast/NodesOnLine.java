package com.simonbaars.clonerefactor.ast;

import com.github.javaparser.ast.Node;

public class NodesOnLine {
	Node node;
	NodeType type;
	
	public NodesOnLine(Node n) {
		this.node = n;
		this.type = NodeType.getNodeType(n);
	}
}
