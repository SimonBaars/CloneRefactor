package com.simonbaars.clonerefactor.ast.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.ReceiverParameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithBlockStmt;
import com.github.javaparser.ast.nodeTypes.NodeWithBody;
import com.github.javaparser.ast.nodeTypes.NodeWithIdentifier;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalBlockStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;
import com.simonbaars.clonerefactor.datatype.map.ListMap;

public interface RequiresNodeOperations {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public default List<Node> childrenToParse(Node parent){
		if(parent instanceof NodeWithOptionalBlockStmt) {
			Optional<BlockStmt> body = ((NodeWithOptionalBlockStmt)parent).getBody();
			return body.isPresent() ? body.get().getChildNodes() : new ArrayList<>(0);
		} else if(parent instanceof NodeWithBody)
			return ((NodeWithBody)parent).getBody().getChildNodes();
		else if (parent instanceof NodeWithBlockStmt)
			return ((NodeWithBlockStmt)parent).getBody().getChildNodes();
		else if (parent.getChildNodes().stream().anyMatch(e -> e instanceof BlockStmt))
			return parent.getChildNodes().stream().flatMap(e -> e instanceof BlockStmt ? e.getChildNodes().stream() : Stream.of(e)).collect(Collectors.toList());
		return parent.getChildNodes();
	}
	
	public default boolean isExcluded(Node n) {
		return n instanceof Expression || n instanceof Modifier || n instanceof NodeWithIdentifier || n instanceof Comment || n instanceof Type || n instanceof AnnotationMemberDeclaration || n instanceof Parameter || n instanceof ReceiverParameter || (n instanceof VariableDeclarator && n.getParentNode().get() instanceof FieldDeclaration);
	}
	
	public default int nodeDepth(Node n) {
		if(n.getParentNode().isPresent())
			return nodeDepth(n.getParentNode().get())+1;
		return 0;
	}
	
	public default ListMap<Integer, Node> depthMap(List<Node> nodes){
		ListMap<Integer, Node> nodeDepths = new ListMap<>();
		nodes.forEach(n -> nodeDepths.addTo(nodeDepth(n), n));
		return nodeDepths;
	}
	
	public default List<Node> lowestNodes(List<Node> nodes) {
		return depthMap(nodes).entrySet().stream().reduce((e1, e2) -> e1.getKey() < e2.getKey() ? e1 : e2).get().getValue();
	}
}
