package com.simonbaars.clonerefactor.refactoring.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.simonbaars.clonerefactor.ast.interfaces.CalculatesLineSize;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;

public class PostMetrics implements RequiresNodeContext, CalculatesLineSize {
	private final int addedTokenVolume;
	private final int addedLineVolume;
	private final int addedNodeVolume;
	
	private final int unitInterfaceSize;
	
	public PostMetrics(MethodDeclaration newMethod, Optional<ClassOrInterfaceDeclaration> classOrInterface, List<Statement> methodcalls) {
		addedTokenVolume = calculateAddedTokenVolume(classOrInterface, newMethod, methodcalls);
		addedLineVolume = calculateAddedLineVolume(classOrInterface, newMethod, methodcalls);
		addedNodeVolume = calculateAddedNodeVolume(classOrInterface, newMethod, methodcalls);
		unitInterfaceSize = newMethod.getParameters().size();
	}
	
	private int calculateAddedTokenVolume(Optional<ClassOrInterfaceDeclaration> classOrInterface,
			MethodDeclaration newMethod, List<Statement> methodcalls) {
		return calculateAddedVolume(this::countTokens, classOrInterface, newMethod, methodcalls);
	}
	
	private int calculateAddedLineVolume(Optional<ClassOrInterfaceDeclaration> classOrInterface,
			MethodDeclaration newMethod, List<Statement> methodcalls) {
		return calculateAddedVolume(this::lineSize, classOrInterface, newMethod, methodcalls);
	}
	
	private int calculateAddedNodeVolume(Optional<ClassOrInterfaceDeclaration> classOrInterface,
			MethodDeclaration newMethod, List<Statement> methodcalls) {
		return calculateAddedVolume(this::amountOfNodes, classOrInterface, newMethod, methodcalls);
	}
	
	public int amountOfNodes(Node n) {
		return 1;
	}
	
	private int calculateAddedVolume(Function<Node, Integer> calculateMetric, Optional<ClassOrInterfaceDeclaration> classOrInterface,
			MethodDeclaration newMethod, List<Statement> methodcalls) {
		int total = 0;
		if(classOrInterface.isPresent()) {
			total += calculateMetric.apply(classOrInterface.get());
		} else {
			total += calculateMetric.apply(newMethod);
		}
		total += methodcalls.stream().mapToInt(calculateMetric::apply).sum();
		return total;
	}

	public int getAddedTokenVolume() {
		return addedTokenVolume;
	}

	public int getAddedLineVolume() {
		return addedLineVolume;
	}

	public int getAddedNodeVolume() {
		return addedNodeVolume;
	}

	public int getUnitInterfaceSize() {
		return unitInterfaceSize;
	}
}
