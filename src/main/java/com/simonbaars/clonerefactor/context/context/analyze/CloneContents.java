package com.simonbaars.clonerefactor.context.context.analyze;

import static com.simonbaars.clonerefactor.context.context.enums.ContentsType.FULLCLASS;
import static com.simonbaars.clonerefactor.context.context.enums.ContentsType.FULLCONSTRUCTOR;
import static com.simonbaars.clonerefactor.context.context.enums.ContentsType.FULLENUM;
import static com.simonbaars.clonerefactor.context.context.enums.ContentsType.FULLINTERFACE;
import static com.simonbaars.clonerefactor.context.context.enums.ContentsType.FULLMETHOD;
import static com.simonbaars.clonerefactor.context.context.enums.ContentsType.ONLYFIELDS;
import static com.simonbaars.clonerefactor.context.context.enums.ContentsType.OTHER;
import static com.simonbaars.clonerefactor.context.context.enums.ContentsType.PARTIALCONSTRUCTOR;
import static com.simonbaars.clonerefactor.context.context.enums.ContentsType.PARTIALMETHOD;
import static com.simonbaars.clonerefactor.context.context.enums.ContentsType.SEVERALMETHODS;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.clonegraph.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.context.context.enums.ContentsType;
import com.simonbaars.clonerefactor.context.context.interfaces.DeterminesMetric;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.LocationContents;

public class CloneContents implements DeterminesMetric<ContentsType>, RequiresNodeOperations {
	@Override
	public ContentsType get(Sequence sequence) {
		return get(sequence.getAny().getContents());
	}

	public ContentsType get(LocationContents c) {
		List<Node> nodes = c.getNodes();
		Node firstNode = nodes.get(0);
		Node lastNode = nodes.get(nodes.size()-1);
		Node lastStatement = getLastStatement(firstNode);
		if(firstNode instanceof MethodDeclaration && lastNode == lastStatement)
			return FULLMETHOD;
		else if(isPartial(MethodDeclaration.class, firstNode, lastNode))
			return PARTIALMETHOD;
		else if(firstNode instanceof ConstructorDeclaration && lastNode == lastStatement)
			return FULLCONSTRUCTOR;
		else if(isPartial(ConstructorDeclaration.class, firstNode, lastNode))
			return PARTIALCONSTRUCTOR;
		else if(nodes.stream().allMatch(e -> getMethod(e).isPresent()))
			return SEVERALMETHODS;
		else if(nodes.stream().allMatch(e -> e instanceof FieldDeclaration)) 
			return ONLYFIELDS;
		else if(firstNode instanceof ClassOrInterfaceDeclaration && !((ClassOrInterfaceDeclaration)firstNode).isInterface() && lastNode == lastStatement)
			return FULLCLASS;
		else if(firstNode instanceof ClassOrInterfaceDeclaration && ((ClassOrInterfaceDeclaration)firstNode).isInterface() && lastNode == lastStatement)
			return FULLINTERFACE;
		else if(firstNode instanceof EnumDeclaration && lastNode == lastStatement)
			return FULLENUM;
		return OTHER;
	}
	
	private<T extends Node> boolean isPartial(Class<T> type, Node n1, Node n2) {
		Optional<T> constructor = getNode(type, n1);
		if(constructor.isPresent()) {
			Optional<T> constructor2 = getNode(type, n2);
			if(constructor2.isPresent() && constructor.get() == constructor2.get()) {
				return true;
			} 
		}
		return false;
	}

	private Node getLastStatement(Node n) {
		List<Node> children = n.getChildNodes();
		Optional<Node> reduce = children.stream().filter(e -> !isExcluded(e)).reduce((first, second) -> second);
		if(reduce.isPresent()) {
			n = reduce.get();
			if(!n.getChildNodes().isEmpty())
				return getLastStatement(n);
		}
		return n;
	}
}
