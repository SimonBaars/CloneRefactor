package com.simonbaars.clonerefactor.refactoring.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.simonbaars.clonerefactor.ast.interfaces.CalculatesLineSize;
import com.simonbaars.clonerefactor.metrics.collectors.CyclomaticComplexityCalculator;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;

public class PostMetrics implements RequiresNodeContext, CalculatesLineSize {
	private final Map<MethodDeclaration, Integer> cc = new HashMap<>();
	private final Map<MethodDeclaration, Integer> size = new HashMap<>();
	
	private final int addedTokenVolume;
	private final int addedLineVolume;
	private final int addedNodeVolume;
	
	public PostMetrics(MethodDeclaration newMethod, Optional<ClassOrInterfaceDeclaration> classOrInterface, List<Statement> methodcalls) {
		for(Statement methodcall : methodcalls) {
			Optional<MethodDeclaration> locationMethod = getMethod(methodcall);
			if(locationMethod.isPresent()) {
				cc.put(locationMethod.get(), new CyclomaticComplexityCalculator().calculate(locationMethod.get()));
				size.put(locationMethod.get(), new CyclomaticComplexityCalculator().calculate(locationMethod.get()));
			}
		}
		addedTokenVolume = calculateAddedTokenVolume(classOrInterface, newMethod, methodcalls);
		addedLineVolume = calculateAddedLineVolume(classOrInterface, newMethod, methodcalls);
	}
	
	private int calculateAddedTokenVolume(Optional<ClassOrInterfaceDeclaration> classOrInterface,
			MethodDeclaration newMethod, List<Statement> methodcalls) {
		return calculateAddedVolume(this::countTokens, classOrInterface, newMethod, methodcalls);
	}
	
	private int calculateAddedLineVolume(Optional<ClassOrInterfaceDeclaration> classOrInterface,
			MethodDeclaration newMethod, List<Statement> methodcalls) {
		return calculateAddedVolume(this::lineSize, classOrInterface, newMethod, methodcalls);
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

	public Map<MethodDeclaration, Integer> getCc() {
		return cc;
	}
	
	public Map<MethodDeclaration, Integer> getSize() {
		return size;
	}
}
