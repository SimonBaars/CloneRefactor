package com.simonbaars.clonerefactor.context.context.interfaces;

import com.simonbaars.clonerefactor.detection.model.Sequence;

public interface DeterminesMetric<E> extends RequiresNodeContext {
	public E get(Sequence sequence);
}
