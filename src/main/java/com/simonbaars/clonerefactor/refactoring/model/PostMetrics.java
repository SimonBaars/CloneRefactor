package com.simonbaars.clonerefactor.refactoring.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.simonbaars.clonerefactor.metrics.collectors.CyclomaticComplexityCalculator;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.FiltersTokens;
import com.simonbaars.clonerefactor.model.Sequence;

public class PostMetrics implements RequiresNodeContext, FiltersTokens {
	private final Map<MethodDeclaration, Integer> cc = new HashMap<>();
	private final Map<MethodDeclaration, Integer> size = new HashMap<>();
	
	private final int addedTokenVolume;
	
	public PostMetrics(MethodDeclaration newMethod, Optional<ClassOrInterfaceDeclaration> classOrInterface, List<Statement> methodcalls) {
		for(Statement methodcall : methodcalls) {
			Optional<MethodDeclaration> locationMethod = getMethod(methodcall);
			if(locationMethod.isPresent()) {
				cc.put(locationMethod.get(), new CyclomaticComplexityCalculator().calculate(locationMethod.get()));
				size.put(locationMethod.get(), new CyclomaticComplexityCalculator().calculate(locationMethod.get()));
			}
		}
		addedTokenVolume = calculateAddedVolume(classOrInterface, newMethod, methodcalls);
	}
	
	private int calculateAddedVolume(Optional<ClassOrInterfaceDeclaration> classOrInterface,
			MethodDeclaration newMethod, List<Statement> methodcalls) {
		int total = 0;
		if(classOrInterface.isPresent()) {
			total += countTokens(classOrInterface.get());
		} else {
			total += Math.toIntExact(getEffectiveTokens(newMethod).count());
		}
		total += methodcalls.stream().mapToInt(n -> countTokens(n)).sum();
		return total;
	}

	public Map<MethodDeclaration, Integer> getCc() {
		return cc;
	}
	
	public Map<MethodDeclaration, Integer> getSize() {
		return size;
	}
}
