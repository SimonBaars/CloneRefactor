package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.Settings;
import com.simonbaars.clonerefactor.compare.CloneType;
import com.simonbaars.clonerefactor.compare.Compare;
import com.simonbaars.clonerefactor.detection.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.ChecksThresholds;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class Type2Variability implements CalculatesPercentages, ChecksThresholds {
	public List<Sequence> determineVariability(Sequence s) {
		List<List<Compare>> literals = createLiteralList(s);
		int[][] equalityArray = createEqualityArray(literals);
		if(globalThresholdsMet(equalityArray, s.getSequence().stream().mapToInt(e -> e.getContents().getTokens().size()).sum())) // We first check the thresholds for the entire sequence. If those are not met, we will try to create smaller sequences
			return Collections.singletonList(s);
		return findAllValidSubSequences(s, literals, equalityArray);
	}

	private List<Sequence> findAllValidSubSequences(Sequence s, List<List<Compare>> literals, int[][] equalityArray) {
		Map<Integer, int[][]> statementEqualityArrays = findConnectedStatements(s, literals, equalityArray);
		List<Sequence> outputSequences = sliceSequence(s, statementEqualityArrays);
		List<List<Integer>> connections = findConnectedSequences(equalityArray);
		List<Sequence> connectionOutput = determineOutput(s, connections);
		return outputSequences;
	}
	
	// From: https://stackoverflow.com/questions/1670862/obtaining-a-powerset-of-a-set-in-java
	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
	    Set<Set<T>> sets = new HashSet<Set<T>>();
	    if (originalSet.isEmpty()) {
	        sets.add(new HashSet<T>());
	        return sets;
	    }
	    List<T> list = new ArrayList<T>(originalSet);
	    T head = list.get(0);
	    Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
	    for (Set<T> set : powerSet(rest)) {
	        Set<T> newSet = new HashSet<T>();
	        newSet.add(head);
	        newSet.addAll(set);
	        sets.add(newSet);
	        sets.add(set);
	    }       
	    return sets;
	}
	
	private List<Sequence> sliceSequence(Sequence s, Map<Integer, int[][]> statementEqualityArrays) {
		List<WeightedPercentage> calcPercentages = getWeightedPercentages(s, statementEqualityArrays);
		List<Sequence> sequences = new ArrayList<>();
		List<WeightedPercentage> percentagesList = new ArrayList<>();
		for(int i = 0; i<calcPercentages.size(); i++) {
			percentagesList.add(calcPercentages.get(i));
			if(calcAvg(percentagesList) > Settings.get().getType2VariabilityPercentage() && !canFixIt(calcPercentages, percentagesList, i) || i+1 == calcPercentages.size()) {
				if(percentagesList.size()>1) {
					percentagesList.remove(percentagesList.size()-1);
					Sequence newSeq = createSequence(s, calcPercentages.indexOf(percentagesList.get(0)), calcPercentages.indexOf(percentagesList.get(percentagesList.size()-1)));
					if(checkThresholds(newSeq))
						sequences.add(newSeq);
				}
				percentagesList.clear();
			}
		}
		return sequences;
	}

	private Sequence createSequence(Sequence s, int from, int to) {
		Sequence newSeq = new Sequence();
		for(Location l : s.getSequence()) {
			Location l2 = new Location(l);
			newSeq.add(l2);
			List<Node> myNodes = l2.getContents().getNodes();
			for(int i = myNodes.size()-1; i>=0; i--)
				if(i<from || i>to)
					myNodes.remove(i);
			Range r = new Range(myNodes.get(0).getRange().get().begin, findNodeLocation(getStatementLoc(l2), myNodes.get(myNodes.size()-1)).getRange().end);
			l2.setRange(r);
			l2.getContents().setRange(r);
			l2.getContents().getCompare().removeIf(e -> e.getRange().isBefore(r.begin) || e.getRange().isAfter(r.end));
			l2.getContents().getTokens().removeIf(e -> e.getRange().get().isBefore(r.begin) || e.getRange().get().isAfter(r.end));
		}
		return newSeq;
	}
	
	public Location getStatementLoc(Location l) {
		if(l.getNextLine() != null)
			return l.getNextLine().getPrevLine();
		return l.getPrevLine().getNextLine();
	}
	
	private Location findNodeLocation(Location l, Node n) {
		if(l.getContents().getNodes().get(0) == n)
			return l;
		return findNodeLocation(l.getNextLine(), n);
	}

	private List<WeightedPercentage> getWeightedPercentages(Sequence s, Map<Integer, int[][]> statementEqualityArrays) {
		List<WeightedPercentage> calcPercentages = new ArrayList<>();
		for(int currNodeIndex = 0; currNodeIndex<s.getAny().getContents().getNodes().size(); currNodeIndex++) {
			int[][] equality = statementEqualityArrays.get(currNodeIndex);
			calcPercentages.add(new WeightedPercentage(diffPerc(equality), equality[0].length));
		}
		return calcPercentages;
	}

	private boolean canFixIt(List<WeightedPercentage> calcPercentages, List<WeightedPercentage> percentagesList,
			int i) {
		percentagesList = new ArrayList<>(percentagesList);
		for(i++; i<calcPercentages.size(); i++) {
			percentagesList.add(calcPercentages.get(i));
			if(calcAvg(percentagesList) <= Settings.get().getType2VariabilityPercentage()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a per statement equality array.
	 * @param s
	 * @param equalityArray
	 * @return
	 */
	private Map<Integer, int[][]> findConnectedStatements(Sequence s, List<List<Compare>> literals, int[][] equalityArray) {
		Map<Integer, int[][]> statementEqualityArrays = new HashMap<>();
		for(int currNodeIndex = 0, startCompareIndex = 0, currCompareIndex = 0; currNodeIndex<s.getAny().getContents().getNodes().size(); currNodeIndex++) {
			for(;currCompareIndex<literals.get(0).size() && getLocationForNode(s.getAny(), currNodeIndex).getRange().contains(literals.get(0).get(currCompareIndex).getRange()); currCompareIndex++);
			statementEqualityArrays.put(currNodeIndex, new int[s.size()][currCompareIndex-startCompareIndex]);
			for(int locationIndex = 0; locationIndex<s.size(); locationIndex++) {
				for(int compareIndex = startCompareIndex; compareIndex<currCompareIndex; compareIndex++) {
					statementEqualityArrays.get(currNodeIndex)[locationIndex][compareIndex-startCompareIndex] = equalityArray[locationIndex][compareIndex];
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
		return getLocation(l.getNextLocation(), node-1);
	}
	
	private List<Sequence> groupSequencesOnBasisOfConnectedStatements(List<List<Integer>> statements){
		for(List<Integer> statement : statements) {
			if(statement.size()>1) {
				
			}
		}
		return null;
	}
	
	private List<List<Integer>> findConnectedStatements(int[][] arr) {
		Graph g = new Graph(arr[0].length);
		for(int i = 0; i<arr.length-1; i++) {
			//for(int j = i+1; j<arr.length; j++) {
				for(int k = 0; k<arr[i].length; k++){
					if(arr[i][k] == arr[i+1][k]) {
						g.addEdge(k, i);
						//g.addEdge(k, j);
					}
				}
			//}
		}
		return g.connectedComponents();
	}

	private boolean globalThresholdsMet(int[][] equalityArray, int total) {
		return diffPerc(equalityArray)<=Settings.get().getType2VariabilityPercentage();
	}

	private List<Sequence> determineOutput(Sequence s, List<List<Integer>> connections) {
		List<Sequence> output = new ArrayList<>();
		for(List<Integer> connection : connections) {
			if(connection.size()>1) {
				Sequence seq = new Sequence();
				for(Integer i : connection) {
					seq.add(s.getSequence().get(i));
				}
				output.add(seq);
			}
		}
		return output;
	}

	private List<List<Integer>> findConnectedSequences(int[][] equalityArray) {
		Graph g = new Graph(equalityArray.length);
		for(int i = 0; i<equalityArray.length; i++) {
			for(int j = i+1; j<equalityArray.length; j++) {
				if((equalityArray[i].length == 0 && equalityArray[j].length == 0) || diffPerc(equalityArray[i], equalityArray[j])<=Settings.get().getType2VariabilityPercentage()) {
					g.addEdge(i, j);
				}
			}
		}
		return g.connectedComponents();
	}

	private int[][] createEqualityArray(List<List<Compare>> literals) {
		int[][] equalityArray = new int[literals.size()][literals.get(0).size()];
		for(int j = 0; j<literals.get(0).size(); j++) {
			final List<Compare> differentCompareLiterals = new ArrayList<>();
			int curr = 0;
			for(int i = 0; i<literals.size(); i++) {
				int index = differentCompareLiterals.indexOf(literals.get(i).get(j));
				if(index == -1) {
					equalityArray[i][j] = curr++;
					differentCompareLiterals.add(literals.get(i).get(j));
				} else {
					equalityArray[i][j] = index;
				}
			}
		}
		return equalityArray;
	}

	private List<List<Compare>> createLiteralList(Sequence s) {
		List<List<Compare>> literals = new ArrayList<>();
		for(Location l : s.getSequence()) {
			List<Compare> literals2 = l.getContents().getType2Comparables();
			literals.add(literals2);
			literals2.forEach(e -> e.setCloneType(CloneType.TYPE1));
		}
		return literals;
	}
}
