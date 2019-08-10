package com.simonbaars.clonerefactor.scripts.thresholds;

import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.scripts.RunOnCorpus;
import com.simonbaars.clonerefactor.scripts.model.MetricsTables;
import com.simonbaars.clonerefactor.settings.Settings;

public class TryTokenThresholds implements Runnable {
	public static void main(String[] args) {
		new TryTokenThresholds().run();
	}

	@Override
	public void run() {
		MetricsTables metricsTables = new MetricsTables();
		System.out.println("Try Thresholds");
		for(int i = 1; i<150; i++) {
			Settings.get().setMinAmountOfTokens(i);
			Metrics metrics = new RunOnCorpus().calculateMetricsForCorpus();
			metricsTables.reportMetrics(Integer.toString(i), metrics);
		}
	}
}
