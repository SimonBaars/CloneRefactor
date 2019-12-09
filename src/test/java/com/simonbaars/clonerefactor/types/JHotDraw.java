package com.simonbaars.clonerefactor.types;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.context.enums.LocationType;
import com.simonbaars.clonerefactor.context.enums.RelationType;
import com.simonbaars.clonerefactor.core.util.SavePaths;
import com.simonbaars.clonerefactor.datatype.map.CountMap;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.helper.Type1Test;
import com.simonbaars.clonerefactor.refactoring.enums.RefactoringStrategy;
import com.simonbaars.clonerefactor.scripts.intimals.IntimalsReader;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternSequence;
import com.simonbaars.clonerefactor.scripts.intimals.similarity.Intersects;
import com.simonbaars.clonerefactor.scripts.intimals.similarity.MatchType;
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
		List<PatternSequence> patternSequences = new IntimalsReader().loadIntimalsClones();
		List<Similarity> cloneToPattern = new Similarity().determineSimilarities(patternSequences, cloneDetection.getClones(), true);
		List<Similarity> patternToClone = new Similarity().determineSimilarities(patternSequences, cloneDetection.getClones(), false);
		CountMap<Double> cloneToPatternSimilarityPerc = new CountMap<>();
		CountMap<Double> patternToCloneSimilarityPerc = new CountMap<>();
		cloneToPattern.forEach(e -> cloneToPatternSimilarityPerc.increment(e.similarityPercentage()));
		patternToClone.forEach(e -> patternToCloneSimilarityPerc.increment(e.similarityPercentage()));
		CountMap<Double> cloneToPatternIntersectPerc = new CountMap<>();
		CountMap<Double> patternToCloneIntersectPerc = new CountMap<>();
		cloneToPattern.forEach(e -> cloneToPatternIntersectPerc.increment(e.intersectPercentage()));
		patternToClone.forEach(e -> patternToCloneIntersectPerc.increment(e.intersectPercentage()));
		CountMap<Integer> cloneToPatternNumIntersect = new CountMap<>();
		CountMap<Integer> patternToCloneNumIntersect = new CountMap<>();
		cloneToPattern.forEach(e -> cloneToPatternNumIntersect.increment(e.intersectNum()));
		patternToClone.forEach(e -> patternToCloneNumIntersect.increment(e.intersectNum()));
		CountMap<LocationType> mostMatchedLocation = new CountMap<>();
		CountMap<RelationType> mostMatchedRelation = new CountMap<>();
		cloneToPattern.stream().filter(e -> e.similarityPercentage()>0D).forEach(e -> mostMatchedRelation.increment(e.getClone().getRelation().getType()));
		patternToClone.stream().flatMap(e -> e.getMatches().stream()).filter(e -> e instanceof Intersects).map(e -> (Intersects)e).forEach(e -> mostMatchedLocation.increment(e.getClone().getLocationType()));
		CountMap<MatchType> cloneToPatternMatchType = new CountMap<>();
		CountMap<MatchType> patternToCloneMatchType = new CountMap<>();
		cloneToPattern.stream().flatMap(e -> e.getMatches().stream()).forEach(e -> cloneToPatternMatchType.increment(e.getMatchType()));
		patternToClone.stream().flatMap(e -> e.getMatches().stream()).forEach(e -> patternToCloneMatchType.increment(e.getMatchType()));
		try {
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"cloneToPatternSimilarityPerc.txt"), cloneToPatternSimilarityPerc.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"patternToCloneSimilarityPerc.txt"), patternToCloneSimilarityPerc.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"cloneToPatternIntersectPerc.txt"), cloneToPatternIntersectPerc.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"patternToCloneIntersectPerc.txt"), patternToCloneIntersectPerc.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"sims1NumIntersect.txt"), cloneToPatternNumIntersect.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"sims2NumIntersect.txt"), patternToCloneNumIntersect.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"mostMatchedLocation.txt"), mostMatchedLocation.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"mostMatchedRelation.txt"), mostMatchedRelation.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"cloneToPatternMatchType.txt"), cloneToPatternMatchType.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"patternToCloneMatchType.txt"), patternToCloneMatchType.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
	