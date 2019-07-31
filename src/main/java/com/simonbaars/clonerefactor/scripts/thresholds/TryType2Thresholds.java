package com.simonbaars.clonerefactor.scripts.thresholds;

import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.scripts.RunOnCorpus;
import com.simonbaars.clonerefactor.scripts.model.MetricsTables;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class TryType2Thresholds implements Runnable {
	public static void main(String[] args) {
		new TryType2Thresholds().run();
	}

	@Override
	public void run() {
		MetricsTables metricsTables = new MetricsTables();
		System.out.println("Try Threshold Percentages");
		Settings.get().setCloneType(CloneType.TYPE2);
		for(int i = 0; i<=100; i++) {
			Settings.get().setType2VariabilityPercentage(i);
			Metrics metrics = new RunOnCorpus().startCorpusCloneDetection();
			metricsTables.reportMetrics(Integer.toString(i), metrics);
		}
	}
}
