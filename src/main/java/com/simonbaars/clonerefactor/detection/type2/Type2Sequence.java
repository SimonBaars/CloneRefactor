package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.interfaces.ChecksThresholds;

public class Type2Sequence implements CalculatesPercentages, ChecksThresholds {
	private final List<Type2Location> statements;

	public Type2Sequence() {
		statements = new ArrayList<>();
	}
	
	public Type2Sequence(List<Type2Location> statementsWithinThreshold) {
		statements = statementsWithinThreshold;
	}

	public List<Type2Location> getSequence() {
		return statements;
	}
	
	public int size() {
		return statements.size();
	}
	
	public void tryToExpand() {
		while(tryToExpand(true) || tryToExpand(false));
	}

	//TODO: Right
	private boolean tryToExpand(boolean left) {
		List<Type2Location> prevs = new ArrayList<>();
		for(Type2Location location : statements) {
			if(location.getPrev() == null)
				return false;
			prevs.add(location.getPrev());
		}
		Type2Location firstPrev = prevs.get(0);
		for(int i = 1; i<prevs.size(); i++) {
			final int j = i;
			if(firstPrev.getContents().getEqualityMap().keySet().stream().anyMatch(e -> e.getStatements().contains(prevs.get(j))))
				return false;
		}
		List<Type2Location> locs = IntStream.range(0,prevs.size()).boxed().map(i -> new Type2Location(prevs.get(i), statements.get(i))).collect(Collectors.toList());
		return checkType2VariabilityThreshold(calculateVariability(locs));
	}
	
	public double calculateVariability(List<Type2Location> statements) {
		List<int[][]> fullContents = statements.stream().map(Type2Location::getFullContents).collect(Collectors.toList());
		List<WeightedPercentage> percentages = new ArrayList<>();
		for(int statementIndex = 0; statementIndex<fullContents.get(0).length; statementIndex++) {
			for(int locationIndex1 = 0; locationIndex1<fullContents.size(); locationIndex1++) {
				for(int locationIndex2 = locationIndex1+1; locationIndex2<fullContents.size(); locationIndex2++) {
					percentages.add(new WeightedPercentage(diffPerc(fullContents.get(locationIndex1)[statementIndex], fullContents.get(locationIndex2)[statementIndex]), fullContents.get(0)[statementIndex].length));
				}
			}
		}
		return calcAvg(percentages);
	}
}
