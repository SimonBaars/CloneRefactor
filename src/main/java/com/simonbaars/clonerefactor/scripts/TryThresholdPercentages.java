package com.simonbaars.clonerefactor.scripts;

import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.scripts.model.MetricsTables;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class TryThresholdPercentages implements Runnable {
	public static void main(String[] args) {
		new TryThresholdPercentages().run();
	}

	@Override
	public void run() {
		MetricsTables metricsTables = new MetricsTables();
		System.out.println("Try Threshold Percentages");
		Settings.get().setCloneType(CloneType.TYPE2);
		for(int i = 0; i<=100; i++) {
			Settings.get().setType2VariabilityPercentage(i);
			Metrics metrics = new RunOnCorpus().startCorpusCloneDetection();
			if(metrics != null)
				metricsTables.collectMetrics(Integer.toString(i), metrics);
			metricsTables.writeTables();
		}
	}
}
