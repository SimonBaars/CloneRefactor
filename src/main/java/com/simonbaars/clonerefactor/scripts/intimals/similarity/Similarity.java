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

	private final List<Matching> matches;
	
	private final PatternSequence pattern;
	private final Sequence clone;
	
	public Similarity() {
		matches = new ArrayList<>();
		this.pattern = null;
		this.clone = null;
	}

	public Similarity(List<Matching> matches, PatternSequence pattern, Sequence clone) {
		super();
		this.matches = matches;
		this.pattern = pattern;
		this.clone = clone;
	}

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
		return new Similarity(determineSimilarity(pattern.getLocations(), clone.getLocations(), fromClone), pattern, clone);
	}

	private List<Matching> determineSimilarity(List<PatternLocation> patterns, List<Location> clones, boolean fromClone) {
		if(fromClone)
			return clones.stream().map(clone -> determineInstanceSimilarity(patterns, clone)).collect(Collectors.toList());
		return patterns.stream().map(pattern -> determineInstanceSimilarity(pattern, clones)).collect(Collectors.toList());
	}
	
	private static Matching determineInstanceSimilarity(PatternLocation pattern, List<Location> clones) {
		return new Intersects().getMostImportant(clones.stream().map(clone -> determineInstanceSimilarity(pattern, clone, false)));
	}
	
	private static Matching determineInstanceSimilarity(List<PatternLocation> patterns, Location clone) {
		return new Intersects().getMostImportant(patterns.stream().map(pattern -> determineInstanceSimilarity(pattern, clone, true)));
	}
	
	private static Matching determineInstanceSimilarity(PatternLocation pattern, Location clone, boolean fromClone) {
		if (pattern.getFile().equals(clone.getFile()) && pattern.actualRange().overlapsWith(clone.getRange()))
			return new Intersects(pattern, clone);
		return new NotSimilar(fromClone ? clone : pattern);
	}
	
	public boolean isMoreImportant(Similarity similarity) {
		return similarityPercentage() > similarity.similarityPercentage();
	}
	
	public double similarityPercentage() {
		WeightedPercentage wp = new WeightedPercentage(0, 0);
		for(Matching match : matches)
			wp = wp.mergeWith(new WeightedPercentage(match.getMatchPercentage(), match.getWeight()));
		return wp.getPercentage(); 
	}
	
	public List<Matching> getMatches(){
		return matches;
	}
	
	public double intersectPercentage() {
		return calcPercentage(intersectNum(), matches.size());
	}
	
	public int intersectNum() {
		return Math.toIntExact(matches.stream().filter(e -> e instanceof Intersects).count());
	}

	public PatternSequence getPattern() {
		return pattern;
	}

	public Sequence getClone() {
		return clone;
	}

	@Override
	public String toString() {
		return "Similarity [matches=" + Arrays.toString(matches.toArray()) + "]";
	}
}
