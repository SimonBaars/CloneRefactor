package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	
	public int[] getLocationArray() {
		return statements.stream().mapToInt(e -> e.getLocationIndex()).sorted().toArray();
	}
	
	public void tryToExpand(List<Type2Sequence> sequences) {
		while(tryToExpand(true).canContinue());
		while(tryToExpand(false).canContinue());
	}

	private ExpandResult tryToExpand(boolean left) {
		List<Type2Location> expandedRow = determineExpandedRow(left);
		if(expandedRow.isEmpty() || !allRowsEqual(expandedRow)) return ExpandResult.IMPOSSIBLE;
		while(true) {
			List<Type2Location> locs = IntStream.range(0,expandedRow.size()).boxed().map(i -> 
				new Type2Location(left ? expandedRow.get(i) : statements.get(i), left ? statements.get(i) : expandedRow.get(i))
			).collect(Collectors.toList());
			if(checkType2VariabilityThreshold(calculateVariability(statements))) {
				IntStream.range(0,expandedRow.size()).forEach(i -> statements.set(i, locs.get(i)));
				return ExpandResult.SUCCESS;
			}
		}
	}

	private boolean allRowsEqual(List<Type2Location> expandedRow) {
		Type2Location firstPrev = expandedRow.get(0);
		for(int i = 1; i<expandedRow.size(); i++) {
			final int j = i;
			if(firstPrev.getFirstContents().getEqualityMap().keySet().stream().anyMatch(e -> e.getStatements().contains(expandedRow.get(j))))
				return false;
		}
		return true;
	}

	private List<Type2Location> determineExpandedRow(boolean left) {
		List<Type2Location> expandedRow = new ArrayList<>();
		for(Type2Location location : statements) {
			if(!left) location = location.getLast();
			if(left ? location.getPrev() == null : location.getNext() == null)
				return Collections.emptyList();
			expandedRow.add(left ? location.getPrev() : location.getNext());
		}
		return expandedRow;
	}
	
	public double calculateVariability(List<Type2Location> statements) {
		List<int[][]> fullContents = statements.stream().map(Type2Location::getFullContents).collect(Collectors.toList());
		List<WeightedPercentage> percentages = new ArrayList<>();
		for(int statementIndex = 0; statementIndex<fullContents.size(); statementIndex++) {
			for(int locationIndex1 = 0; locationIndex1<fullContents.get(statementIndex).length; locationIndex1++) {
				for(int locationIndex2 = locationIndex1+1; locationIndex2<fullContents.get(statementIndex).length; locationIndex2++) {
					percentages.add(new WeightedPercentage(diffPerc(fullContents.get(statementIndex)[locationIndex1], fullContents.get(statementIndex)[locationIndex2]), fullContents.get(statementIndex)[locationIndex1].length));
				}
			}
		}
		return calcAvg(percentages);
	}
	
	public Sequence convertToSequence(Sequence s) {
		return new Sequence(statements.stream().map(e -> e.convertToLocation(s)).collect(Collectors.toList())).isValid();
	}

	@Override
	public String toString() {
		return "Type2Sequence [statements=" + Arrays.toString(statements.toArray()) + "]";
	}
}
