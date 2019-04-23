package com.simonbaars.clonerefactor.metrics.enums;

import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.FULLMETHOD;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.PARTIALMETHOD;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.SEVERALMETHODS;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.ast.NodeParser;
import com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneContents implements MetricEnum<ContentsType> {
	public enum ContentsType{
		FULLMETHOD, 
		PARTIALMETHOD, 
		SEVERALMETHODS, 
		ONLYFIELDS, 
		INCLUDESFIELDS, 
		FULLCLASS, 
		HASCLASSDECLARATION, 
		HASINTERFACEDECLARATION, 
		HASENUMDECLARATION, 
		HASENUMFIELDS;
	}

	@Override
	public ContentsType get(Sequence sequence) {
		List<Node> nodes = sequence.getAny().getContents().getNodes();
		if(nodes.get(0) instanceof MethodDeclaration && nodes.get(nodes.size()-1) == getLastStatement((MethodDeclaration)nodes.get(0))) {
			return FULLMETHOD;
		} else if(getMethod(nodes.get(0)) == getMethod(nodes.get(nodes.size()-1))) {
			return PARTIALMETHOD;
		} else if(nodes.stream().allMatch(e -> getMethod(e)!=null)) {
			return SEVERALMETHODS;
		} else if(nodes.stream().allMatch(e -> getMethod(e)== null && e instanceof FieldDeclaration)) {
			return ONLYFIELDS;
		}
		return null;
	}

	private Node getLastStatement(MethodDeclaration methodDeclaration) {
		List<Node> children = methodDeclaration.getChildNodes();
		if(children.get(children.size()-1) instanceof BodyDeclaration)
			children = children.get(children.size()-1).getChildNodes();
		return children.stream().filter(e -> !NodeParser.isExcluded(e)).reduce((first, second) -> second).get();
	}
}
