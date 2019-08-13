package com.simonbaars.clonerefactor.scripts.thresholds;

import com.simonbaars.clonerefactor.context.Metrics;
import com.simonbaars.clonerefactor.scripts.RunOnCorpus;
import com.simonbaars.clonerefactor.scripts.model.MetricsTable;
import com.simonbaars.clonerefactor.settings.Settings;

public class TryStatementThresholds implements Runnable {
	public static void main(String[] args) {
		new TryStatementThresholds().run();
	}

	@Override
	public void run() {
		MetricsTable metricsTables = new MetricsTable();
		System.out.println("Try Statement Thresholds");
		for(int i = 1; i<20; i++) {
			Settings.get().setMinAmountOfNodes(i);
			Metrics metrics = new RunOnCorpus().calculateMetricsForCorpus();
			metricsTables.reportMetrics(Integer.toString(i), metrics);
		}
	}
}
