package com.simonbaars.clonerefactor.misc;

import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.scripts.RunOnCorpus;
import com.simonbaars.clonerefactor.scripts.model.MetricsTables;

import junit.framework.TestCase;

public class RunTest extends TestCase {
	public void testMetricTables() {
		MetricsTables metricsTables = new MetricsTables();
		Metrics metrics = new RunOnCorpus().calculateMetricsForCorpus();
		metricsTables.reportMetrics("testcolumn", metrics);
	}
}
