package com.simonbaars.clonerefactor.metrics.context;

import com.simonbaars.clonerefactor.model.Sequence;

@SuppressWarnings("rawtypes")
public interface MetricEnum<E extends Enum> extends RequiresNodeContext {
	public E get(Sequence sequence);
}
