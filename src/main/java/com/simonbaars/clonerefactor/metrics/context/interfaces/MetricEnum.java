package com.simonbaars.clonerefactor.metrics.context.interfaces;

import com.simonbaars.clonerefactor.model.Sequence;

public interface MetricEnum<E> extends RequiresNodeContext {
	public E get(Sequence sequence);
}
