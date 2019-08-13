package com.simonbaars.clonerefactor.scripts.thresholds;

import com.simonbaars.clonerefactor.context.Metrics;
import com.simonbaars.clonerefactor.scripts.RunOnCorpus;
import com.simonbaars.clonerefactor.scripts.model.MetricsTable;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class TryType3Thresholds implements Runnable {
	public static void main(String[] args) {
		new TryType3Thresholds().run();
	}

	@Override
	public void run() {
		MetricsTable metricsTables = new MetricsTable();
		System.out.println("Try Type 3 Thresholds");
		Settings.get().setCloneType(CloneType.TYPE3R);
		for(int i = 1; i<=100; i++) {
			Settings.get().setType3GapSize(i*2);
			Metrics metrics = new RunOnCorpus().calculateMetricsForCorpus();
			metricsTables.reportMetrics(Integer.toString(i), metrics);
		}
	}
}
