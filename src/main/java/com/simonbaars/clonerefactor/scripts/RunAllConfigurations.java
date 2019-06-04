package com.simonbaars.clonerefactor.scripts;

import java.util.Arrays;

import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Scope;
import com.simonbaars.clonerefactor.settings.Settings;

public class RunAllConfigurations {
	
	public static void main(String[] args) {
		System.out.println("RunAllConfigurations");
		
		CloneType[] cloneTypes = CloneType.values();
		Scope[] scopes = Scope.values();
		configureSettings(cloneTypes, scopes);
		
		for(int i = 1; i<40; i++) {
			Settings.get().setMinAmountOfTokens(i);
			RunOnCorpus.main(args);
			rotate(cloneTypes, scopes);
		}
	}

	private static void configureSettings(CloneType[] cloneTypes, Scope[] scopes) {
		Settings.get().setCloneType(cloneTypes[0]);
		Settings.get().setScope(scopes[0]);
		Settings.get().setUseLiteratureTypeDefinitions(false);
		Settings.get().setMinAmountOfLines(1);
		Settings.get().setMinAmountOfTokens(10);
		Settings.get().setMinAmountOfNodes(1);
	}

	private static void rotate(CloneType[] cloneTypes, Scope[] scopes) {
		int curCloneType = Arrays.binarySearch(cloneTypes, Settings.get().getCloneType());
		int curScope = Arrays.binarySearch(scopes, Settings.get().getScope());
		if(Settings.get().isUseLiteratureTypeDefinitions()) {
			if(curCloneType>curScope) {
				curScope ++;
			} else curCloneType ++;
		}
		Settings.get().setUseLiteratureTypeDefinitions(!Settings.get().isUseLiteratureTypeDefinitions());
		Settings.get().setCloneType(cloneTypes[curCloneType]);
		Settings.get().setScope(scopes[curScope]);
	}
}
