package com.simonbaars.clonerefactor.ast;

import com.github.javaparser.ast.Node;

public class NodeRegistry {
	Node node;
	NodeType type;
	
	public NodeRegistry(Node n) {
		this.node = n;
		this.type = NodeType.getNodeType(n);
	}
}
