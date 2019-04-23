package com.simonbaars.clonerefactor.metrics.enums;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
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
			
		}
		return null;
	}

	private Node getLastStatement(MethodDeclaration methodDeclaration) {
		// TODO Auto-generated method stub
		return null;
	}
}
