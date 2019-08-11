package com.simonbaars.clonerefactor.scripts;

import com.simonbaars.clonerefactor.scripts.model.MetricsTable;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class RunAllTypes {
	
	public static void main(String[] args) {
		System.out.println("RunAllTypes");
		
		Settings.get().setCloneType(CloneType.values()[0]);
		MetricsTable table = new MetricsTable();
		
		do table.reportMetrics(Settings.get().getCloneType()+" "+Settings.get().getScope(), new RunOnCorpus().calculateMetricsForCorpus());
		while (rotate());
	}

	private static boolean rotate() {
		int ordinal = Settings.get().getCloneType().ordinal()+1;
		if(ordinal == CloneType.values().length)
			return false;
		Settings.get().setCloneType(CloneType.values()[ordinal]);
		return true;
	}
}
