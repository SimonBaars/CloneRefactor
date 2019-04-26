package com.simonbaars.clonerefactor.metrics.enums;

import com.simonbaars.clonerefactor.ast.RequiresNodeOperations;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.model.Sequence;

public class CloneRefactorability implements MetricEnum<Refactorability>, RequiresNodeOperations {
	public enum Refactorability{
		EASY, 
		HARD
	}

	@Override
	public Refactorability get(Sequence sequence) {
		return Refactorability.EASY;
	}
}
