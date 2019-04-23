package com.simonbaars.clonerefactor.metrics.enums;

import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.FULLCLASS;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.FULLENUM;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.FULLINTERFACE;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.FULLMETHOD;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.HASCLASSDECLARATION;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.HASENUMDECLARATION;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.HASENUMFIELDS;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.HASINTERFACEDECLARATION;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.INCLUDESFIELDS;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.MIXED;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.ONLYFIELDS;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.PARTIALMETHOD;
import static com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType.SEVERALMETHODS;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.simonbaars.clonerefactor.ast.NodeParser;
import com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneContents implements MetricEnum<ContentsType> {
	public enum ContentsType{
		FULLMETHOD, 
		PARTIALMETHOD, 
		SEVERALMETHODS, 
		ONLYFIELDS, 
		FULLCLASS, 
		FULLINTERFACE,
		FULLENUM,
		HASCLASSDECLARATION, 
		HASINTERFACEDECLARATION, 
		HASENUMDECLARATION, 
		HASENUMFIELDS,
		INCLUDESFIELDS,
		MIXED;
	}

	@Override
	public ContentsType get(Sequence sequence) {
		List<Node> nodes = sequence.getAny().getContents().getNodes();
		System.out.println(Arrays.toString(nodes.toArray()));
		System.out.println(nodes.stream().map(e -> e.getClass().toString()).collect(Collectors.joining(", ")));
		if(nodes.get(0) instanceof MethodDeclaration && nodes.get(nodes.size()-1) == getLastStatement(nodes.get(0))) {
			return FULLMETHOD;
		} else if(getMethod(nodes.get(0))!=null && getMethod(nodes.get(0)) == getMethod(nodes.get(nodes.size()-1))) {
			return PARTIALMETHOD;
		} else if(nodes.stream().allMatch(e -> getMethod(e)!=null)) {
			return SEVERALMETHODS;
		} else if(nodes.stream().allMatch(e -> getMethod(e)== null && e instanceof FieldDeclaration)) {
			return ONLYFIELDS;
		} else if(nodes.get(0) instanceof ClassOrInterfaceDeclaration && !((ClassOrInterfaceDeclaration)nodes.get(0)).isInterface() && nodes.get(nodes.size()-1) == getLastStatement(nodes.get(0))) {
			return FULLCLASS;
		} else if(nodes.get(0) instanceof ClassOrInterfaceDeclaration && ((ClassOrInterfaceDeclaration)nodes.get(0)).isInterface() && nodes.get(nodes.size()-1) == getLastStatement(nodes.get(0))) {
			return FULLINTERFACE;
		} else if(nodes.get(0) instanceof EnumDeclaration && nodes.get(nodes.size()-1) == getLastStatement(nodes.get(0))) {
			return FULLENUM;
		} else if(nodes.stream().anyMatch(e -> e instanceof ClassOrInterfaceDeclaration && !((ClassOrInterfaceDeclaration)e).isInterface())) {
			return HASCLASSDECLARATION;
		} else if(nodes.stream().anyMatch(e -> e instanceof ClassOrInterfaceDeclaration && ((ClassOrInterfaceDeclaration)e).isInterface())) {
			return HASINTERFACEDECLARATION;
		} else if(nodes.stream().anyMatch(e -> e instanceof EnumDeclaration)) {
			return HASENUMDECLARATION;
		} else if(nodes.stream().anyMatch(e -> e instanceof EnumConstantDeclaration)) {
			return HASENUMFIELDS;
		} else if(nodes.stream().anyMatch(e -> getMethod(e)== null && e instanceof FieldDeclaration)) {
			return INCLUDESFIELDS;
		}
		return MIXED;
	}

	private Node getLastStatement(Node n) {
		List<Node> children = n.getChildNodes();
		if(children.get(children.size()-1) instanceof BlockStmt)
			children = children.get(children.size()-1).getChildNodes();
		Optional<Node> reduce = children.stream().filter(e -> !NodeParser.isExcluded(e)).reduce((first, second) -> second);
		return reduce.isPresent() ? reduce.get() : n;
	}
}
