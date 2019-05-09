package com.simonbaars.clonerefactor.detection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.simonbaars.clonerefactor.Settings;
import com.simonbaars.clonerefactor.compare.CloneType;
import com.simonbaars.clonerefactor.compare.Compare;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class Type2Variability {
	public List<Sequence> determineVariability(Sequence s) {
		System.out.println("in 1 sequence with "+s.size()+" locs");
		List<List<Compare>> literals = createLiteralList(s);
		int[][] equalityArray = createEqualityArray(literals);
		System.out.println(Arrays.deepToString(equalityArray));
		List<List<Integer>> connections = findConnectedSequences(equalityArray);
		System.out.println("== CONNECT "+connections.size()+" ==");
		connections.forEach(e -> System.out.println(Arrays.toString(e.toArray())));
		return determineOutput(s, connections);
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
		System.out.println("out "+output.size()+" sequence with "+output.stream().map(e -> Integer.toString(e.size())).collect(Collectors.joining(", "))+" locs");
		return output;
	}

	private List<List<Integer>> findConnectedSequences(int[][] equalityArray) {
		Graph g = new Graph(equalityArray.length);
		for(int i = 0; i<equalityArray.length; i++) {
			for(int j = i+1; j<equalityArray.length; j++) {
				if((equalityArray[i].length == 0 &&  equalityArray[j].length == 0) || diffPerc(equalityArray[i], equalityArray[j])<=Settings.get().getType2VariabilityPercentage()) {
					g.addEdge(i, j);
					System.out.println("Added Edge "+i+", "+j);
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
			List<Compare> literals2 = l.getContents().getType2Threshold();
			System.out.println(l.getContents().compareTypes());
			System.out.println("Of "+l.getContents().getCompare().size()+" filter to "+literals2.size());
			literals.add(literals2);
			literals2.forEach(e -> e.setCloneType(CloneType.TYPE1));
		}
		return literals;
	}
	
	public int diffPerc(int[] arr1, int[] arr2) {
		int same = 0, diff = 0;
		for(int i = 0; i<arr1.length; i++){
			if(arr1[i] == arr2[i]) same++; 
			else diff++;
		}
		return calcPercentage(diff, same+diff);
	}
	
	public int calcPercentage(int part, int whole) {
		return Math.round((float)part/(float)whole*100F);
	}
}
