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
		List<Similarity> sims1 = new Similarity().determineSimilarities(patternSequences, cloneDetection.getClones(), true);
		List<Similarity> sims2 = new Similarity().determineSimilarities(patternSequences, cloneDetection.getClones(), false);
		System.out.println(sims1);
		System.out.println(sims2);
		CountMap<Double> sims1SimilarityPerc = new CountMap<>();
		CountMap<Double> sims2SimilarityPerc = new CountMap<>();
		sims1.forEach(e -> sims1SimilarityPerc.increment(e.similarityPercentage()));
		sims2.forEach(e -> sims2SimilarityPerc.increment(e.similarityPercentage()));
		CountMap<Double> sims1IntersectPerc = new CountMap<>();
		CountMap<Double> sims2IntersectPerc = new CountMap<>();
		sims1.forEach(e -> sims1IntersectPerc.increment(e.intersectPercentage()));
		sims2.forEach(e -> sims2IntersectPerc.increment(e.intersectPercentage()));
		CountMap<Integer> sims1NumIntersect = new CountMap<>();
		CountMap<Integer> sims2NumIntersect = new CountMap<>();
		sims1.forEach(e -> sims1NumIntersect.increment(e.intersectNum()));
		sims2.forEach(e -> sims2NumIntersect.increment(e.intersectNum()));
		CountMap<LocationType> mostMatchedLocation = new CountMap<>();
		CountMap<RelationType> mostMatchedRelation = new CountMap<>();
		sims2.stream().filter(e -> e.similarityPercentage()>0D).forEach(e -> mostMatchedRelation.increment(e.getClone().getRelation().getType()));
		sims2.stream().flatMap(e -> e.getMatches().stream()).filter(e -> e instanceof Intersects).map(e -> (Intersects)e).forEach(e -> mostMatchedLocation.increment(e.getClone().getLocationType()));
		try {
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"sims1SimilarityPerc.txt"), sims1SimilarityPerc.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"sims2SimilarityPerc.txt"), sims2SimilarityPerc.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"sims1IntersectPerc.txt"), sims1IntersectPerc.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"sims2IntersectPerc.txt"), sims2IntersectPerc.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"sims1NumIntersect.txt"), sims1NumIntersect.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"sims2NumIntersect.txt"), sims2NumIntersect.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"mostMatchedLocation.txt"), mostMatchedLocation.toString());
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"mostMatchedRelation.txt"), mostMatchedRelation.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
	