package com.simonbaars.clonerefactor.detection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simonbaars.clonerefactor.Settings;
import com.simonbaars.clonerefactor.compare.CloneType;
import com.simonbaars.clonerefactor.compare.Compare;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class Type2Variability implements CalculatesPercentages {
	public List<Sequence> determineVariability(Sequence s) {
		List<List<Compare>> literals = createLiteralList(s);
		int[][] equalityArray = createEqualityArray(literals);
		if(globalThresholdsMet(equalityArray, s.getSequence().stream().mapToInt(e -> e.getContents().getTokens().size()).sum())) // We first check the thresholds for the entire sequence. If those are not met, we will try to create smaller sequences
			return Collections.singletonList(s);
		Map<Integer, int[][]> statementEqualityArrays = findConnectedStatements(s, literals, equalityArray);
		List<Sequence> outputSequences = sliceSequence(s, statementEqualityArrays);
		List<List<Integer>> connections = findConnectedSequences(equalityArray);
		return determineOutput(s, connections);
	}
	
	private List<Sequence> sliceSequence(Sequence s, Map<Integer, int[][]> statementEqualityArrays) {
		List<WeightedPercentage> calcPercentages = new ArrayList<>();
		for(int currNodeIndex = 0; currNodeIndex<s.getAny().getContents().getNodes().size(); currNodeIndex++) {
			int[][] equality = statementEqualityArrays.get(currNodeIndex);
			calcPercentages.add(new WeightedPercentage(diffPerc(equality), equality[0].length));
		}
		List<Sequence> sequences = new ArrayList<>();
		
		return null;
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
		return getLocation(l.getNextLine().getPrevLine() /*magic trick*/, node);
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
