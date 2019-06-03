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
	
	public int[] locationArray() {
		return statements.stream().mapToInt(e -> e.getLocationIndex()).sorted().toArray();
	}
	
	public Object[] transformedEqualityArray(boolean left, int transform) {
		return statements.stream().sorted().map(e -> new int[] {e.getLocationIndex(), left ? e.getStatementIndices().getStart()-transform : e.getStatementIndices().getEnd()+transform}).toArray();
	}
	
	public void tryToExpand(List<Type2Sequence> sequences) {
		while(tryToExpand(sequences, true));
		while(tryToExpand(sequences, false));
	}

	private boolean tryToExpand(List<Type2Sequence> sequences, boolean left) {
		List<Type2Location> expandedRow = checkSequenceExpansionOpportunities(sequences, determineExpandedRow(left), left);
		if(expandedRow.isEmpty() || !allRowsEqual(expandedRow)) return false;
		while(true) {
			List<Type2Location> locs = mergeLocations(expandedRow, statements, left);
			if(checkType2VariabilityThreshold(calculateVariability(statements))) {
				IntStream.range(0,expandedRow.size()).forEach(i -> statements.set(i, locs.get(i)));
				return true;
			}
		}
	}
	
	private Type2Location mergeLocations(Type2Location loc1, Type2Location loc2, boolean left) {
		return new Type2Location(left ? loc1 : loc2, left ? loc2 : loc1);
	}
	
	public List<Type2Location> mergeLocations(List<Type2Location> l1, List<Type2Location> l2, boolean left){
		return IntStream.range(0,l1.size()).boxed().map(i -> mergeLocations(l1.get(i), l2.get(i), left)).collect(Collectors.toList());
	}

	private List<Type2Location> checkSequenceExpansionOpportunities(List<Type2Sequence> clones, List<Type2Location> expandedRow, boolean left) {
		Type2Sequence expanded = new Type2Sequence(expandedRow);
		for(Type2Sequence clone : clones) {
			if(Arrays.deepEquals(clone.transformedEqualityArray(left, 0), expanded.transformedEqualityArray(!left, 1))) {
				return mergeLocations(clone.getSequence(), expanded.getSequence(), left);
			}
		}
		return expandedRow;
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
