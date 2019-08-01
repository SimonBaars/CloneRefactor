package com.simonbaars.clonerefactor.ast;

import com.simonbaars.clonerefactor.SequenceObserver;
import com.simonbaars.clonerefactor.metrics.MetricCollector;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.model.Sequence;

public class MetricObserver implements SequenceObserver {
	
	private final MetricCollector collector;

	public MetricObserver(MetricCollector collector) {
		super();
		this.collector = collector;
	}

	@Override
	public void update(ProblemType problem, Sequence sequence, int problemSize) {
		collector.getMetrics().incrementGeneralStatistic(problem+" Amount", 1);
		collector.getMetrics().incrementGeneralStatistic(problem+" Total Size", problemSize);
		collector.getMetrics().averages.addTo(problem.toString(), problemSize);
	}

}
