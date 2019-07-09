package com.simonbaars.clonerefactor.refactoring.target;

import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.simonbaars.clonerefactor.model.Sequence;

public class ExtractToNewInterface extends ExtractToClassOrInterface {
	private static int x = 0;
	
	public ExtractToNewInterface(Sequence s) {
		super(new ClassOrInterfaceDeclaration(new NodeList<>(), new NodeList<>(), true, new SimpleName("GeneratedInterface"+(x++)), new NodeList<>(), new NodeList<>(), new NodeList<>(), new NodeList<>()));
		Set<ClassOrInterfaceDeclaration> classOrInterface = s.getLocations().stream().map(l -> getClass(l.getContents().getNodes().get(0))).collect(Collectors.toSet());
		ClassOrInterfaceType implementedType = new JavaParser().parseClassOrInterfaceType(getClassOrInterface().getNameAsString()).getResult().get();
		classOrInterface.stream().filter(c -> c.getImplementedTypes().stream().noneMatch(t -> t.getNameAsString().equals(implementedType.getNameAsString()))).forEach(c -> c.addImplementedType(implementedType));
	}
}
