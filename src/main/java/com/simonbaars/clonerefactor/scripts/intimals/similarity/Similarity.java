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
		return getMostImportant(clones.stream().map(clone -> determineSimilarity(pattern, clone, false)));
	}
	
	private Similarity determineSimilarity(PatternSequence pattern, Sequence clone, boolean fromClone) {
		List<Matching> matching = determineSimilarity(pattern.getLocations(), clone.getLocations(), fromClone);
		return null;
	}

	public Similarity determineSimilarity(List<PatternSequence> patterns, Sequence clone) {
		return getMostImportant(patterns.stream().map(pattern -> determineSimilarity(pattern, clone, true)));
	}

	private List<Matching> determineSimilarity(List<PatternLocation> patterns, List<Location> clones, boolean fromClone) {
		if(fromClone)
			return clones.stream().map(clone -> determineInstanceSimilarity(patterns, clone)).collect(Collectors.toList());
		return patterns.stream().map(pattern -> determineInstanceSimilarity(pattern, clones)).collect(Collectors.toList());
	}
	
	private static Matching determineInstanceSimilarity(PatternLocation pattern, List<Location> clones) {
		if (pattern.actualRange().overlapsWith(clone.getRange())) {
			return new Intersects(pattern, clone);
		}
		return new NotSimilar();
	}
	
	private static Matching determineInstanceSimilarity(List<PatternLocation> patterns, Location clone) {
		if (pattern.actualRange().overlapsWith(clone.getRange())) {
			return new Intersects(pattern, clone);
		}
		return new NotSimilar().getMostImportant(stuff);
	}
	
	private static Matching determineInstanceSimilarity(PatternLocation pattern, Location clone) {
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
