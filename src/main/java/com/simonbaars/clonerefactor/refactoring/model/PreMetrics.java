package com.simonbaars.clonerefactor.refactoring.model;

import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;

public class PreMetrics implements RequiresNodeContext {
	private final int totalTokens;
	private final int totalNodes;
	private final int totalLines;
	
	public PreMetrics(Sequence s) {
		this.totalTokens = s.getTotalTokenVolume();
		this.totalNodes = s.getTotalNodeVolume();
		this.totalLines = s.getTotalEffectiveLineVolume();
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
