package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.detection.type2.model.WeightedPercentage;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternLocation;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternSequence;

public class Similarity implements CalculatesPercentages, HasImportance<Similarity> {

	private final List<Intersects> matches = new ArrayList<>();
	private int clonesNoMatch;
	private int patternNoMatch;
	
	public Similarity() {}
	
	public List<Similarity> determineSimilarities(List<PatternSequence> patterns, List<Sequence> clones, boolean fromClone) {
		if(fromClone)
			return clones.stream().map(clone -> determineSimilarity(patterns, clone)).collect(Collectors.toList());
		return patterns.stream().map(pattern -> determineSimilarity(pattern, clones)).collect(Collectors.toList());
	}
	
	public Similarity determineSimilarity(PatternSequence pattern, List<Sequence> clones) {
		return getMostImportant(clones.stream().map(clone -> determineSimilarity(pattern, clone)));
		List<Similarity> similarities = new ArrayList<>();
		for(Sequence clone : clones) {
			similarities.add(determineSimilarity(pattern, clone));
		}
		return getMostImportant(similarities);
	}
	
	public Similarity determineSimilarity(List<PatternSequence> patterns, Sequence clone) {
		List<Similarity> similarities = new ArrayList<>();
		for(PatternSequence pattern : patterns) {
			similarities.add(determineSimilarity(pattern, clone));
		}
		return similarities.stream().reduce((e1, e2) -> e1.isMoreImportant(e2) ? e1 : e2).get();
	}

	private Similarity determineSimilarity(PatternSequence pattern, Sequence clone) {
		for(PatternLocation location : pattern.getLocations()) {
			
		}
		
		if(!pattern.getFile().equals(clone.getFile())) {
			return new NotSimilar();
		} else if (pattern.actualRange().overlapsWith(clone.getRange())) {
			return new Intersects(pattern, clone);
		}
		return new SameClass(pattern, clone);
	}
	
	private static Matching determineSimilarity(PatternLocation pattern, List<Location> clones) {
		if (pattern.actualRange().overlapsWith(clone.getRange())) {
			return new Intersects(pattern, clone);
		}
		return new NotSimilar();
	}
	
	private static Matching determineSimilarity(List<PatternLocation> patterns, Location clone) {
		if (pattern.actualRange().overlapsWith(clone.getRange())) {
			return new Intersects(pattern, clone);
		}
		return new NotSimilar().getMostImportant(stuff);
	}
	
	private static Matching determineSimilarity(PatternLocation pattern, Location clone) {
		if (pattern.actualRange().overlapsWith(clone.getRange())) {
			return new Intersects(pattern, clone);
		}
		return new NotSimilar();
	}
	
	public boolean isMoreImportant(Similarity similarity) {
		return matchPercentage() > similarity.matchPercentage();
	}
	
	private double matchPercentage() {
		WeightedPercentage wp = new WeightedPercentage(0, clonesNoMatch+patternNoMatch);
		matches.forEach(match -> wp.mergeWith(new WeightedPercentage(match.getDifferencePercentage(), 1)));
		return wp.getPercentage(); 
	}
}
