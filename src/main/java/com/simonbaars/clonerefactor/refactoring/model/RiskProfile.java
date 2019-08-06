package com.simonbaars.clonerefactor.refactoring.model;

import java.util.EnumMap;
import java.util.Map;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.utils.Pair;
import com.simonbaars.clonerefactor.datatype.map.CountMap;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.metrics.context.enums.Risk;

public class RiskProfile {
	private final Map<Risk, Integer> riskCC = new EnumMap<Risk, Integer>(Risk.class);
	private final CountMap<Pair<Risk, Risk>> bucketChange = new CountMap<Pair<Risk, Risk>>();
	private final ProblemType type;
	private final Risk newMethodRisk;
	private final int newMethodProblemSize;
	
	public RiskProfile(ProblemType type, int newMethodProblemSize) {
		super();
		this.type = type;
		this.newMethodProblemSize = newMethodProblemSize;
		this.newMethodRisk = type.getRisk(newMethodProblemSize);
	}
	
	public Map<Risk, Integer> getRiskCC() {
		return riskCC;
	}
	
	public CountMap<Pair<Risk, Risk>> getBucketChange() {
		return bucketChange;
	}
	
	public ProblemType getType() {
		return type;
	}
	
	public Risk getNewMethodRisk() {
		return newMethodRisk;
	}

	public int getNewMethodProblemSize() {
		return newMethodProblemSize;
	}

	public RiskProfile calculateRisk(Map<MethodDeclaration, Integer> methodCC2, Map<MethodDeclaration, Integer> methodCC3) {
		methodCC2.entrySet().forEach(e -> {
			if(methodCC3.containsKey(e.getKey())) {
				Integer preRiskAmount = methodCC3.get(e.getKey());
				Risk preRisk = type.getRisk(preRiskAmount);
				Risk afterRisk = type.getRisk(e.getValue());
				getBucketChange().increment(new Pair<Risk, Risk>(preRisk, afterRisk));
				getRiskCC().put(preRisk, e.getValue() - preRiskAmount);
			}
		});
		return this;
	}
}
