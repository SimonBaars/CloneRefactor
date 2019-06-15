package com.simonbaars.clonerefactor.detection.type2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.interfaces.ChecksForComparability;
import com.simonbaars.clonerefactor.detection.interfaces.ChecksThresholds;
import com.simonbaars.clonerefactor.model.Sequence;

public class Type2Sequence implements CalculatesPercentages, ChecksThresholds, ChecksForComparability {
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
	
	public void tryToExpand(List<Type2Sequence> clones) {
		tryToExpand(clones, true);
		tryToExpand(clones, false);
	}

	private void tryToExpand(List<Type2Sequence> clones, boolean left) {
		List<Type2Location> curStatements = new ArrayList<>(statements);
		List<Type2Sequence> mergedClones = new ArrayList<>();
		while(!(curStatements = determineExpandedRow(curStatements, left)).isEmpty()) {
			curStatements = checkSequenceExpansionOpportunities(mergedClones, clones, curStatements, left);
			if(curStatements.isEmpty() || !allRowsComparable(curStatements)) return;
			if(checkType2VariabilityThreshold(calculateVariability(curStatements))) {
				this.statements.clear();
				this.statements.addAll(curStatements);
				clones.removeAll(mergedClones);
				mergedClones.clear();
			} 
		}
	}
	
	private Type2Location mergeLocations(Type2Location loc1, Type2Location loc2, boolean left) {
		return new Type2Location(left ? loc1 : loc2, left ? loc2 : loc1);
	}
	
	public List<Type2Location> mergeLocations(List<Type2Location> l1, List<Type2Location> l2, boolean left){
		return IntStream.range(0,l1.size()).boxed().map(i -> mergeLocations(l1.get(i), l2.get(i), left)).collect(Collectors.toList());
	}

	private List<Type2Location> checkSequenceExpansionOpportunities(List<Type2Sequence> mergedClones, List<Type2Sequence> clones, List<Type2Location> expandedRow, boolean left) {
		Type2Sequence expanded = new Type2Sequence(expandedRow);
		for(Type2Sequence clone : clones) {
			if(Arrays.deepEquals(clone.transformedEqualityArray(left, 0), expanded.transformedEqualityArray(!left, 1))) {
				mergedClones.add(clone);
				if(IntStream.range(0,expanded.getSequence().size()).anyMatch(i -> expanded.getSequence().get(i).getLocationIndex() != clone.getSequence().get(i).getLocationIndex()))
					return Collections.emptyList();
				return mergeLocations(expanded.getSequence(), clone.getSequence(), left);
			}
		}
		return expandedRow;
	}

	private boolean allRowsComparable(List<Type2Location> expandedRow) {
		for(int i = 1; i<expandedRow.size(); i++) {
			if(!isComparable(expandedRow.get(0), expandedRow.get(i)))
				return false;
		}
		return true;
	}

	private List<Type2Location> determineExpandedRow(List<Type2Location> curStatements, boolean left) {
		List<Type2Location> expandedRow = new ArrayList<>();
		for(Type2Location location : curStatements) {
			Type2Location origLocation = location;
			if(!left) location = location.getLast();
			if(left ? location.getPrev() == null || location.getPrev().getLocationIndex() != location.getLocationIndex() : location.getNext() == null || location.getNext().getLocationIndex() != location.getLocationIndex())
				return Collections.emptyList();
			expandedRow.add(mergeLocations(left ? location.getPrev() : location.getNext(), origLocation, left));
		}
		return expandedRow;
	}
	
	public double calculateVariability(List<Type2Location> locations) {
		List<WeightedPercentage> percentages = new ArrayList<>();
		for(int locationIndex1 = 0; locationIndex1<locations.size(); locationIndex1++) {
			int[] fullContents1 = locations.get(locationIndex1).contentArray();
			for(int locationIndex2 = locationIndex1+1; locationIndex2<locations.size(); locationIndex2++) {
				int[] fullContents2 = locations.get(locationIndex2).contentArray();
				percentages.add(new WeightedPercentage(diffPerc(fullContents1, fullContents2), fullContents1.length));
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
