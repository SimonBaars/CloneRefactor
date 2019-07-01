package com.simonbaars.clonerefactor.scripts;

import com.simonbaars.clonerefactor.scripts.model.MetricsTables;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Scope;
import com.simonbaars.clonerefactor.settings.Settings;

public class RunSome {
	
	public static void main(String[] args) {
		System.out.println("RunSome");
		
		CloneType[] cloneTypes = CloneType.values();
		MetricsTables table = new MetricsTables();
		
		Settings.get().setScope(Scope.ALL);
		Settings.get().setMinAmountOfLines(1);
		Settings.get().setMinAmountOfTokens(10);
		Settings.get().setMinAmountOfNodes(1);
		
		for(int i = 0; i<cloneTypes.length; i++) {
			Settings.get().setCloneType(cloneTypes[i]);
			for(int j = 0; j<2; j++) {
				Settings.get().setUseLiteratureTypeDefinitions(!Settings.get().isUseLiteratureTypeDefinitions());
				table.collectMetrics("T"+Settings.get().getCloneType().getTypeAsNumber()+(Settings.get().isUseLiteratureTypeDefinitions() ? "" : "R"), new RunOnCorpus().startCorpusCloneDetection());
				table.writeTables();
			}
		}
	}
}
