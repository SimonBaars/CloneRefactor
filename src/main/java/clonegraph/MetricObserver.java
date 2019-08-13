package clonegraph;

import com.simonbaars.clonerefactor.context.MetricCollector;
import com.simonbaars.clonerefactor.context.ProblemType;
import com.simonbaars.clonerefactor.detection.metrics.interfaces.SequenceObserver;
import com.simonbaars.clonerefactor.detection.model.Sequence;

public class MetricObserver implements SequenceObserver {
	
	private final MetricCollector collector;

	public MetricObserver(MetricCollector collector) {
		super();
		this.collector = collector;
	}

	@Override
	public void update(ProblemType problem, Sequence sequence, int problemSize) {
		collector.getMetrics().incrementGeneralStatistic(problem+" Amount", 1);
		collector.getMetrics().incrementGeneralStatistic(metricTotalSize(problem), problemSize);
		collector.getMetrics().averages.addTo(problem.toString(), problemSize);
		if(problem!=ProblemType.DUPLICATION)
			collector.getMetrics().incrementGeneralStatistic(problem, problemSize);
	}

	public static String metricTotalSize(ProblemType problem) {
		return problem+" Total Size";
	}

}
