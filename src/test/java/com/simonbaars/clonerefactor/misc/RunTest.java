package com.simonbaars.clonerefactor.misc;

import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.scripts.RunOnCorpus;
import com.simonbaars.clonerefactor.scripts.model.MetricsTable;

import junit.framework.TestCase;

public class RunTest extends TestCase {
	public void testMetricTables() {
		MetricsTable metricsTables = new MetricsTable();
		Metrics metrics = new RunOnCorpus().calculateMetricsForCorpus();
		metricsTables.reportMetrics("testcolumn", metrics);
	}
}
