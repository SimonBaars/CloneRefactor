package com.simonbaars.clonerefactor.refactoring.model;

import com.simonbaars.clonerefactor.metrics.calculators.CalculatesCyclomaticComplexity;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;

public class PreMetrics implements RequiresNodeContext, CalculatesCyclomaticComplexity {
	private final int tokens;
	private final int nodes;
	private final int lines;
	private final int cc;
	
	public PreMetrics(Sequence s) {
		this.tokens = s.getTotalTokenVolume();
		this.nodes = s.getTotalNodeVolume();
		this.lines = s.getTotalEffectiveLineVolume();
		this.cc = calculateCCIncrease(s.getLocations().stream().flatMap(l -> l.getContents().getTopLevelNodes().stream()));
	}

	public int getTokens() {
		return tokens;
	}

	public int getNodes() {
		return nodes;
	}

	public int getLines() {
		return lines;
	}

	public int getCc() {
		return cc;
	}
}
