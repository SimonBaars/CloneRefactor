package com.simonbaars.clonerefactor;

import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.metrics.ProblemType;

public interface SequenceObserver {
	public void update(ProblemType problem, Sequence sequence, int problemSize);
}
