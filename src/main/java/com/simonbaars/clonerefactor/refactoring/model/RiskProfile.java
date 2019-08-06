package com.simonbaars.clonerefactor.refactoring.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.utils.Pair;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.metrics.context.enums.Risk;

public class RiskProfile {
	private final Map<MethodDeclaration, Pair<Integer, Integer>> changedProblemSize = new HashMap<MethodDeclaration, Pair<Integer, Integer>>();
	private final ProblemType type;
	private final int newMethodProblemSize;
	
	public RiskProfile(ProblemType type, int newMethodProblemSize) {
		super();
		this.type = type;
		this.newMethodProblemSize = newMethodProblemSize;
	}
	
	public ProblemType getType() {
		return type;
	}

	public int getNewMethodProblemSize() {
		return newMethodProblemSize;
	}

	public RiskProfile calculateRisk(Map<MethodDeclaration, Integer> methodCC2, Map<MethodDeclaration, Integer> methodCC3) {
		methodCC2.entrySet().forEach(e -> {
			if(methodCC3.containsKey(e.getKey()))
				changedProblemSize.put(e.getKey(), new Pair<Integer, Integer>(methodCC3.get(e.getKey()), e.getValue()));
		});
		return this;
	}
}
