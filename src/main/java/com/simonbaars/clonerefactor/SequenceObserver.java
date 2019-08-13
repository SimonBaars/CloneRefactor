package com.simonbaars.clonerefactor;

import com.simonbaars.clonerefactor.context.ProblemType;
import com.simonbaars.clonerefactor.detection.model.Sequence;

public interface SequenceObserver {
	public void update(ProblemType problem, Sequence sequence, int problemSize);
}
