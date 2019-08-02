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
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class PostMetrics implements RequiresNodeContext {
	private final Map<MethodDeclaration, Integer> cc = new HashMap<>();
	private final Map<MethodDeclaration, Integer> size = new HashMap<>();
	
	public Map<MethodDeclaration, Integer> getCc() {
		return cc;
	}
	public Map<MethodDeclaration, Integer> getSize() {
		return size;
	}
	
	public void determine(MethodDeclaration newMethod, Optional<ClassOrInterfaceDeclaration> classOrInterface, List<Statement> methodcalls, Sequence s) {
		for(Statement methodcall : methodcalls) {
			Optional<MethodDeclaration> locationMethod = getMethod(methodcall);
			if(locationMethod.isPresent()) {
				cc.put(locationMethod.get(), new CyclomaticComplexityCalculator().calculate(locationMethod.get()));
				size.put(locationMethod.get(), new CyclomaticComplexityCalculator().calculate(locationMethod.get()));
			}
		}
	}
}
