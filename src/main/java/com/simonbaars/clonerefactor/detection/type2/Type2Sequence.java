package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.interfaces.ChecksThresholds;
import com.simonbaars.clonerefactor.model.Sequence;

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

	private boolean tryToExpand(boolean left) {
		List<Type2Location> expandedRow = determineExpandedRow(left);
		if(expandedRow == null || !allRowsEqual(expandedRow)) return false;
		List<Type2Location> locs = IntStream.range(0,expandedRow.size()).boxed().map(i -> new Type2Location(left ? expandedRow.get(i) : statements.get(i).getLast(), left ? statements.get(i) : expandedRow.get(i))).collect(Collectors.toList());
		if(!left) {
			IntStream.range(0,expandedRow.size()).forEach(i -> {
				if(statements.get(i).getMergedWith() == null) statements.set(i, locs.get(i)); 
				else statements.get(i).getSecondToLast().setMergedWith(locs.get(i));
			});
		}
		if(checkType2VariabilityThreshold(calculateVariability(locs))) {
			if(left) IntStream.range(0,expandedRow.size()).forEach(i -> statements.set(i, locs.get(i)));
			return true;
		} else if (!left) {
			statements.forEach(e -> e.getSecondToLast().setMergedWith(null));
		}
		return false;
	}

	private boolean allRowsEqual(List<Type2Location> expandedRow) {
		Type2Location firstPrev = expandedRow.get(0);
		for(int i = 1; i<expandedRow.size(); i++) {
			final int j = i;
			if(firstPrev.getContents().getEqualityMap().keySet().stream().anyMatch(e -> e.getStatements().contains(expandedRow.get(j))))
				return false;
		}
		return true;
	}

	private List<Type2Location> determineExpandedRow(boolean left) {
		List<Type2Location> expandedRow = new ArrayList<>();
		for(Type2Location location : statements) {
			if(!left) location = location.getLast();
			if(left ? location.getPrev() == null : location.getNext() == null)
				return null;
			expandedRow.add(left ? location.getPrev() : location.getNext());
		}
		return expandedRow;
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
	
	public Sequence convertToSequence(Sequence s) {
		return new Sequence(statements.stream().map(e -> e.convertToLocation(s)).collect(Collectors.toList())).isValid();
	}
}
