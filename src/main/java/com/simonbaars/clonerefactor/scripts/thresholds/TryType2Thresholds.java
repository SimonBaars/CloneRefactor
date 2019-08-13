package com.simonbaars.clonerefactor.scripts.thresholds;

import com.simonbaars.clonerefactor.context.Metrics;
import com.simonbaars.clonerefactor.scripts.RunOnCorpus;
import com.simonbaars.clonerefactor.scripts.model.MetricsTable;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class TryType2Thresholds implements Runnable {
	public static void main(String[] args) {
		new TryType2Thresholds().run();
	}

	@Override
	public void run() {
		MetricsTable metricsTables = new MetricsTable();
		System.out.println("Try Threshold Percentages");
		Settings.get().setCloneType(CloneType.TYPE2R);
		for(int i = 0; i<=100; i++) {
			Settings.get().setType2VariabilityPercentage(i);
			Metrics metrics = new RunOnCorpus().calculateMetricsForCorpus();
			metricsTables.reportMetrics(Integer.toString(i), metrics);
		}
	}
}
