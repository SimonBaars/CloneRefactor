package com.simonbaars.clonerefactor.scripts;

import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.scripts.model.MetricsTables;
import com.simonbaars.clonerefactor.settings.Settings;

public class TryThresholds implements Runnable {
	public static void main(String[] args) {
		new TryThresholds().run();
	}

	@Override
	public void run() {
		MetricsTables metricsTables = new MetricsTables();
		System.out.println("Try Thresholds");
		for(int i = 1; i<150; i++) {
			Settings.get().setMinAmountOfTokens(i);
			Metrics metrics = new RunOnCorpus().startCorpusCloneDetection();
			if(metrics != null)
				metricsTables.collectMetrics(Integer.toString(i), metrics);
			metricsTables.writeTables();
		}
	}
}
