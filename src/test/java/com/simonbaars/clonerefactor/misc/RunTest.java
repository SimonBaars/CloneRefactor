package com.simonbaars.clonerefactor.misc;

import org.junit.Assert;
import org.junit.Test;

import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.scripts.RunOnCorpus;
import com.simonbaars.clonerefactor.scripts.model.MetricsTable;

public class RunTest {
	@Test
	public void testMetricTables() {
		MetricsTable metricsTables = new MetricsTable();
		Metrics metrics = new RunOnCorpus().calculateMetricsForCorpus();
		Assert.assertNotNull("Metrics should not be null", metrics);
		metricsTables.reportMetrics("testcolumn", metrics);
	}
}
