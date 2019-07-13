package com.simonbaars.clonerefactor.metrics.context;

import com.simonbaars.clonerefactor.model.Sequence;

@SuppressWarnings("rawtypes")
public interface MetricEnum<MyEnum extends Enum> extends RequiresNodeContext {
	public MyEnum get(Sequence sequence);
}
