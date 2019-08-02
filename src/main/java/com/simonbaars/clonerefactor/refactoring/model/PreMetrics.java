package com.simonbaars.clonerefactor.refactoring.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.metrics.collectors.CyclomaticComplexityCalculator;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class PreMetrics implements RequiresNodeContext {
	private final int totalTokens;
	private final int totalNodes;
	private final int totalLines;
	
	public PreMetrics(Sequence s) {
		this.totalTokens = s.getTotalTokenVolume();
		this.totalNodes = s.getTotalNodeVolume();
		this.totalLines = s.getTotalEffectiveLineVolume();
	}
	
	public Map<MethodDeclaration, Integer> getCc() {
		return cc;
	}
	
	public Map<MethodDeclaration, Integer> getSize() {
		return size;
	}

	public int getTotalTokens() {
		return totalTokens;
	}

	public int getTotalNodes() {
		return totalNodes;
	}

	public int getTotalLines() {
		return totalLines;
	}
}
