package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.compare.Compare;
import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.interfaces.ChecksThresholds;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.settings.CloneType;

public class Type2Variability implements CalculatesPercentages, ChecksThresholds {
	public List<Sequence> determineVariability(Sequence s) {
		List<List<Compare>> literals = createLiteralList(s);
		int[][] equalityArray = createEqualityArray(literals);
		if(globalThresholdsMet(equalityArray, s.getLocations().stream().mapToInt(e -> e.getContents().getTokens().size()).sum())) // We first check the thresholds for the entire sequence. If those are not met, we will try to create smaller sequences
			return Collections.singletonList(s);
		return findAllValidSubSequences(s, literals, equalityArray);
	}

	private List<Sequence> findDisplacedClones(Sequence s, List<List<Compare>> literals, Map<Integer, int[][]> statementEqualityArrays) {
		Type2Location lastLoc = generateType2Locations(statementEqualityArrays);
		List<Type2Sequence> type2Sequences = new Type2CloneDetection().findChains(lastLoc);
		return type2Sequences.stream().map(e -> e.convertToSequence(s)).collect(Collectors.toList());
	}

	private Type2Location generateType2Locations(Map<Integer, int[][]> statementEqualityArrays) {
		final List<Type2Contents> contentsList = new ArrayList<>();
		int amountOfLocations = statementEqualityArrays.values().iterator().next().length;
		Type2Location prevStatement = null;
		for(int locationIndex = 0; locationIndex<amountOfLocations; locationIndex++) {	
			for(Entry<Integer, int[][]> statementEqualityEntry : statementEqualityArrays.entrySet()) {
				int statementIndex = statementEqualityEntry.getKey();
				int[][] equalityArray = statementEqualityEntry.getValue();
						
				int[] locationContents = equalityArray[locationIndex];
				Type2Contents contents = new Type2Contents(locationContents);
				if(contentsList.contains(contents))
					contents = contentsList.get(contentsList.indexOf(contents));
				else contentsList.add(contents);
				final Type2Location statement = new Type2Location(locationIndex, statementIndex, contents, prevStatement);
				contents.getStatements().add(statement);
				if(prevStatement!=null) prevStatement.setNext(statement);
				prevStatement = statement;
			}
		}
		generateWeightedPercentages(contentsList);
		return prevStatement;
	}

	private void generateWeightedPercentages(List<Type2Contents> contentsList) {
		for(int i = 0; i<contentsList.size(); i++) {
			for(int j = i+1; j<contentsList.size(); j++) {
				Type2Contents location1 = contentsList.get(i);
				Type2Contents location2 = contentsList.get(j);
				int[] location1Contents = location1.getContents();
				int[] location2Contents = location2.getContents();
				if(location1Contents.length==location2Contents.length && IntStream.range(0, location1Contents.length).filter(k -> location1Contents[k] < 0 || location2Contents[k] < 0).noneMatch(k -> location1Contents[k]!=location2Contents[k])) {
					WeightedPercentage p = new WeightedPercentage(diffPerc(location1Contents, location2Contents), location1Contents.length);
					location1.getEqualityMap().put(location2, p);
					location2.getEqualityMap().put(location1, p);
				}
			}
		}
	}

	private List<Sequence> findAllValidSubSequences(Sequence s, List<List<Compare>> literals, int[][] equalityArray) {
		Map<Integer, int[][]> statementEqualityArrays = findConnectedStatements(s, literals, equalityArray);
		return findDisplacedClones(s, literals, statementEqualityArrays);
	}

	// https://stackoverflow.com/questions/40201309/best-way-to-get-a-power-set-of-an-array
	public int[][] powerset(int[] a){
		int max = 1 << a.length;
		int[][] result = new int[max][];
		for (int i = 0; i < max; ++i) {
		    result[i] = new int[Integer.bitCount(i)];
		    for (int j = 0, b = i, k = 0; j < a.length; ++j, b >>= 1)
		        if ((b & 1) != 0)
		            result[i][k++] = a[j];
		}
		return result;
	}
	
	public Location getStatementLoc(Location l) {
		if(l.getNext() != null)
			return l.getNext().getPrev();
		return l.getPrev().getNext();
	}

	//Creates a per statement equality array.
	private Map<Integer, int[][]> findConnectedStatements(Sequence s, List<List<Compare>> literals, int[][] equalityArray) {
		Map<Integer, int[][]> statementEqualityArrays = new HashMap<>();
		for(int statementIndex = 0, startCompareIndex = 0, currCompareIndex = 0; statementIndex<s.getAny().getContents().getNodes().size(); statementIndex++) {
			for(;currCompareIndex<literals.get(0).size() && getLocationForNode(s.getAny(), statementIndex).getRange().contains(literals.get(0).get(currCompareIndex).getRange()); currCompareIndex++);
			statementEqualityArrays.put(statementIndex, new int[s.size()][currCompareIndex-startCompareIndex]);
			for(int locationIndex = 0; locationIndex<s.size(); locationIndex++) {
				for(int compareIndex = startCompareIndex; compareIndex<currCompareIndex; compareIndex++) {
					statementEqualityArrays.get(statementIndex)[locationIndex][compareIndex-startCompareIndex] = equalityArray[locationIndex][compareIndex];
				}
			}
			startCompareIndex = currCompareIndex;
		}
		return statementEqualityArrays;
	}
	
	public Location getLocationForNode(Location l, int node) {
		return getLocation(getStatementLoc(l), node);
	}

	private Location getLocation(Location l, int node) {
		if(node <= 0)
			return l;
		return getLocation(l.getNext(), node-1);
	}

	private boolean globalThresholdsMet(int[][] equalityArray, int total) {
		return checkType2VariabilityThreshold(diffPerc(equalityArray));
	}

	private int[][] createEqualityArray(List<List<Compare>> literals) {
		final int START_NUMBER=1; //Basically any non negative number that's not zero.
		int[][] equalityArray = new int[literals.size()][literals.get(0).size()];
		final List<Compare> differentCompareLiterals = new ArrayList<>();
		int curr = START_NUMBER;
		for(int j = 0; j<literals.get(0).size(); j++) {
			for(int i = 0; i<literals.size(); i++) {
				int index = differentCompareLiterals.indexOf(literals.get(i).get(j));
				if(index == -1) {
					equalityArray[i][j] = literals.get(i).get(j).doesType2Compare() ? curr++ : -curr++;
					differentCompareLiterals.add(literals.get(i).get(j));
				} else {
					index=index+START_NUMBER;
					equalityArray[i][j] = literals.get(i).get(j).doesType2Compare() ? index : -index;
				}
			}
		}
		return equalityArray;
	}

	private List<List<Compare>> createLiteralList(Sequence s) {
		List<List<Compare>> literals = new ArrayList<>();
		for(Location l : s.getLocations()) {
			List<Compare> literals2 = l.getContents().getCompare();
			literals.add(literals2);
			literals2.stream().filter(Compare::doesType2Compare).forEach(e -> e.setCloneType(CloneType.TYPE1));
		}
		return literals;
	}
}
