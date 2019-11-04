package com.simonbaars.clonerefactor.types;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.core.util.SavePaths;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.helper.Type1Test;
import com.simonbaars.clonerefactor.refactoring.enums.RefactoringStrategy;
import com.simonbaars.clonerefactor.scripts.intimals.IntimalsReader;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternSequence;
import com.simonbaars.clonerefactor.scripts.intimals.similarity.Similarity;
import com.simonbaars.clonerefactor.scripts.model.MetricsTable;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class JHotDraw extends Type1Test {
	public JHotDraw(String testName) {
		super(testName);
	}

	public void testJHotDraw() {
		MetricsTable tables = new MetricsTable();
		for(CloneType type : CloneType.values()) {
			Settings.get().setCloneType(type);
			SavePaths.genTimestamp();
			Settings.get().setRefactoringStrategy(RefactoringStrategy.DONOTREFACTOR);
	    	System.out.println("JHotDraw");
	    	String path = "/Users/sbaars/Documents/Kim/jhotdraw/";
	    	System.out.println(Settings.get());
			DetectionResults cloneDetection = Main.cloneDetection(Paths.get(path), Paths.get(path+"src/"));
			cloneDetection.sorted();
			tables.reportMetrics(type.getNicelyFormatted(), cloneDetection.getMetrics());
			try {
				writeStringToFile(new File(SavePaths.getMyOutputFolder()+"refactor.txt"), cloneDetection.getRefactorResults().toString());
				writeStringToFile(new File(SavePaths.getMyOutputFolder()+type.getNicelyFormatted()+".txt"), cloneDetection.toString());
				writeStringToFile(new File(SavePaths.getMyOutputFolder()+"settings.txt"), Settings.get().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void type3() {
		MetricsTable tables = new MetricsTable();
		Settings.get().setCloneType(CloneType.TYPE3);
		SavePaths.genTimestamp();
		Settings.get().setRefactoringStrategy(RefactoringStrategy.DONOTREFACTOR);
		System.out.println("JHotDraw");
		String path = "/Users/sbaars/Documents/Kim/jhotdraw/";
		System.out.println(Settings.get());
		DetectionResults cloneDetection = Main.cloneDetection(Paths.get(path), Paths.get(path+"src/"));
		cloneDetection.sorted();
		tables.reportMetrics(CloneType.TYPE3.getNicelyFormatted(), cloneDetection.getMetrics());
		try {
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"refactor.txt"), cloneDetection.getRefactorResults().toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+CloneType.TYPE3.getNicelyFormatted()+".txt"), cloneDetection.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"settings.txt"), Settings.get().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void type3Similarity() {
		Settings.get().setCloneType(CloneType.TYPE3);
		Settings.get().setRefactoringStrategy(RefactoringStrategy.DONOTREFACTOR);
		System.out.println("JHotDraw");
		String path = "/Users/sbaars/Documents/Kim/jhotdraw/";
		DetectionResults cloneDetection = Main.cloneDetection(Paths.get(path), Paths.get(path+"src/"));
		cloneDetection.sorted();
		List<Similarity> fromClone = new ArrayList<>(), fromPattern = new ArrayList<>();
		List<PatternSequence> patternSequences = new IntimalsReader().loadIntimalsClones();
		List<Similarity> sims1 = new Similarity().determineSimilarities(patternSequences, cloneDetection.getClones(), true);
		List<Similarity> sims2 = new Similarity().determineSimilarities(patternSequences, cloneDetection.getClones(), false);
		System.out.println(sims1);
		System.out.println(sims2);
	}
}
	