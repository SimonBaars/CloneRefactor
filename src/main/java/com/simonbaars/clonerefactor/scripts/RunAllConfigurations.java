package com.simonbaars.clonerefactor.scripts;

import java.util.Arrays;

import com.simonbaars.clonerefactor.scripts.model.MetricsTables;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Scope;
import com.simonbaars.clonerefactor.settings.Settings;

public class RunAllConfigurations {
	
	public static void main(String[] args) {
		System.out.println("RunAllConfigurations");
		
		CloneType[] cloneTypes = CloneType.values();
		Scope[] scopes = Scope.values();
		configureSettings(cloneTypes, scopes);
		MetricsTables table = new MetricsTables();
		
		do table.reportMetrics(Settings.get().getCloneType()+" "+Settings.get().getScope(), new RunOnCorpus().calculateMetricsForCorpus());
		while (rotate(cloneTypes, scopes));
	}

	private static void configureSettings(CloneType[] cloneTypes, Scope[] scopes) {
		Settings.get().setCloneType(cloneTypes[0]);
		Settings.get().setScope(scopes[0]);
		Settings.get().setMinAmountOfLines(1);
		Settings.get().setMinAmountOfTokens(10);
		Settings.get().setMinAmountOfNodes(1);
	}

	private static boolean rotate(CloneType[] cloneTypes, Scope[] scopes) {
		int curCloneType = Arrays.binarySearch(cloneTypes, Settings.get().getCloneType());
		int curScope = Arrays.binarySearch(scopes, Settings.get().getScope());
		curScope++;
		if(curScope == scopes.length) {
			curScope = 0;
			curCloneType ++;
		}
		if(curCloneType == cloneTypes.length)
			return false;
		Settings.get().setCloneType(cloneTypes[curCloneType]);
		Settings.get().setScope(scopes[curScope]);
		return true;
	}
}
