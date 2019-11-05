package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import java.util.ArrayList;
import java.util.Arrays;
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
	private int patternsNoMatch;
	
	public Similarity() {}
	
	public List<Similarity> determineSimilarities(List<PatternSequence> patterns, List<Sequence> clones, boolean fromClone) {
		if(fromClone)
			return clones.stream().map(clone -> determineSimilarity(patterns, clone)).collect(Collectors.toList());
		return patterns.stream().map(pattern -> determineSimilarity(pattern, clones)).collect(Collectors.toList());
	}
	
	private Similarity determineSimilarity(PatternSequence pattern, List<Sequence> clones) {
		return getMostImportant(clones.stream().map(clone -> determineSimilarity(pattern, clone, false)));
	}
	
	private Similarity determineSimilarity(List<PatternSequence> patterns, Sequence clone) {
		return getMostImportant(patterns.stream().map(pattern -> determineSimilarity(pattern, clone, true)));
	}
	
	private Similarity determineSimilarity(PatternSequence pattern, Sequence clone, boolean fromClone) {
		List<Matching> matchingClones = determineSimilarity(pattern.getLocations(), clone.getLocations(), true),
						matchingPatterns = determineSimilarity(pattern.getLocations(), clone.getLocations(), false),
						main = fromClone ? matchingClones : matchingPatterns;
		Similarity similarity = new Similarity();
		main.stream().filter(e -> e instanceof Intersects).map(e -> (Intersects)e).forEach(similarity.matches::add);
		similarity.clonesNoMatch = matchingClones.stream().filter(e -> e instanceof NotSimilar).mapToInt(e -> ((NotSimilar)e).lines).sum();
		similarity.patternsNoMatch = matchingClones.stream().filter(e -> e instanceof NotSimilar).mapToInt(e -> ((NotSimilar)e).lines).sum();
		return similarity;
	}

	private List<Matching> determineSimilarity(List<PatternLocation> patterns, List<Location> clones, boolean fromClone) {
		if(fromClone)
			return clones.stream().map(clone -> determineInstanceSimilarity(patterns, clone)).collect(Collectors.toList());
		return patterns.stream().map(pattern -> determineInstanceSimilarity(pattern, clones)).collect(Collectors.toList());
	}
	
	private static Matching determineInstanceSimilarity(PatternLocation pattern, List<Location> clones) {
		return new NotSimilar().getMostImportant(clones.stream().map(clone -> determineInstanceSimilarity(pattern, clone, false)));
	}
	
	private static Matching determineInstanceSimilarity(List<PatternLocation> patterns, Location clone) {
		return new NotSimilar().getMostImportant(patterns.stream().map(pattern -> determineInstanceSimilarity(pattern, clone, true)));
	}
	
	private static Matching determineInstanceSimilarity(PatternLocation pattern, Location clone, boolean fromClone) {
		if (pattern.getFile().equals(clone.getFile()) && pattern.actualRange().overlapsWith(clone.getRange()))
			return new Intersects(pattern, clone);
		return new NotSimilar(fromClone ? clone : pattern);
	}
	
	public boolean isMoreImportant(Similarity similarity) {
		return matchPercentage() > similarity.matchPercentage();
	}
	
	private double matchPercentage() {
		WeightedPercentage wp = new WeightedPercentage(0, clonesNoMatch+patternsNoMatch);
		matches.forEach(match -> wp.mergeWith(new WeightedPercentage(match.getMatchPercentage(), match.getWeight())));
		return wp.getPercentage(); 
	}

	@Override
	public String toString() {
		return "Similarity [matches=" + Arrays.toString(matches.toArray()) + ", clonesNoMatch=" + clonesNoMatch + ", patternsNoMatch="
				+ patternsNoMatch + "]";
	}
}
