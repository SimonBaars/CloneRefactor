package com.simonbaars.clonerefactor.scripts.thresholds;

import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.scripts.RunOnCorpus;
import com.simonbaars.clonerefactor.scripts.model.MetricsTables;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class TryType3Thresholds implements Runnable {
	public static void main(String[] args) {
		new TryType3Thresholds().run();
	}

	@Override
	public void run() {
		MetricsTables metricsTables = new MetricsTables();
		System.out.println("Try Type 3 Thresholds");
		Settings.get().setCloneType(CloneType.TYPE3);
		for(int i = 1; i<=100; i++) {
			Settings.get().setType3GapSize(i*2);
			Metrics metrics = new RunOnCorpus().startCorpusCloneDetection();
			metricsTables.reportMetrics(Integer.toString(i), metrics);
		}
	}
}
