package com.simonbaars.clonerefactor.refactoring.model;

import java.util.EnumMap;
import java.util.Map;

import com.github.javaparser.utils.Pair;
import com.simonbaars.clonerefactor.datatype.map.CountMap;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.metrics.context.enums.Risk;

public class RiskProfile {
	private final Map<Risk, Integer> riskCC = new EnumMap<Risk, Integer>(Risk.class);
	private final CountMap<Pair<Risk, Risk>> bucketChange = new CountMap<Pair<Risk, Risk>>();
	private final ProblemType type;
	
	public RiskProfile(ProblemType type) {
		super();
		this.type = type;
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
}
