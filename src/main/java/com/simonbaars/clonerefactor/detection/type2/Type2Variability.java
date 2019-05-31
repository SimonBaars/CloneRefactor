package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.compare.Compare;
import com.simonbaars.clonerefactor.datatype.CountMap;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.interfaces.ChecksThresholds;
import com.simonbaars.clonerefactor.detection.interfaces.RemovesDuplicates;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.settings.CloneType;

public class Type2Variability implements CalculatesPercentages, ChecksThresholds, RemovesDuplicates {
	public List<Sequence> determineVariability(Sequence s) {
		List<List<Compare>> literals = createLiteralList(s);
		int[][] equalityArray = createEqualityArray(literals);
		if(globalThresholdsMet(equalityArray, s.getSequence().stream().mapToInt(e -> e.getContents().getTokens().size()).sum())) // We first check the thresholds for the entire sequence. If those are not met, we will try to create smaller sequences
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
		Type2Location prevStatement = null;
		for(Entry<Integer, int[][]> statementEqualityEntry : statementEqualityArrays.entrySet()) {
			int statementIndex = statementEqualityEntry.getKey();
			int[][] equalityArray = statementEqualityEntry.getValue();
			for(int locationIndex = 0; locationIndex<equalityArray.length; locationIndex++) {				
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
		//List<Sequence> sequences = new ArrayList<>();
		Map<Integer, int[][]> statementEqualityArrays = findConnectedStatements(s, literals, equalityArray);
		/*for(int[] relevantLocationIndices : powerset(IntStream.range(0, s.size()).toArray())){
			if(relevantLocationIndices.length>1)
				sliceSequence(sequences, s, statementEqualityArrays, relevantLocationIndices);
			else if (relevantLocationIndices.length == 1) 
				//sliceSequence(sequences, s, statementEqualityArrays, new int[] {relevantLocationIndices[0], relevantLocationIndices[0]});
				findInnerClones(sequences, s, statementEqualityArrays, relevantLocationIndices[0]);
		}*/
		return findDisplacedClones(s, literals, statementEqualityArrays);
	}
	
	private void findInnerClones(List<Sequence> sequences, Sequence s, Map<Integer, int[][]> statementEqualityArrays, int index) {
		final ListMap<EqualityArray, Integer> stuffOnLine = new ListMap<>();
		statementEqualityArrays.keySet().stream().forEach(i -> stuffOnLine.addTo(new EqualityArray(statementEqualityArrays.get(i)[index]), i));
		final CountMap<Integer> chainList = new CountMap<>();
		
		for(Integer i : statementEqualityArrays.keySet()) {
			Entry<EqualityArray, List<Integer>> clones = stuffOnLine.getEntryForValue(i);
			ListMap<Integer, Integer> endingChains = new ListMap<>();

			List<Integer> removeChain = new ArrayList<>();
			chainList.entrySet().stream().forEach(curChain -> {
				Optional<Integer> chain = clones.getValue().stream().filter(newClone -> newClone == curChain.getKey()+curChain.getValue()).findAny();
				if(chain.isPresent()) {
					curChain.setValue(curChain.getValue()+1);
					//clones.getValue().remove(chain.get());
				} 
				if(!chain.isPresent() || i == statementEqualityArrays.size()-1){
					endingChains.addTo(curChain.getValue(), curChain.getKey());
					removeChain.add(curChain.getKey());
				}
			});
			removeChain.forEach(chainList::remove);
			
			createSequencesOf(sequences, s, endingChains, index);
			
			clones.getValue().forEach(chainList::increment);
			clones.getValue().remove(i);
		}
	}

	private void createSequencesOf(List<Sequence> sequences, Sequence s, ListMap<Integer, Integer> endingChains, int index) {
		for(Integer i : endingChains.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
			List<Integer> e = endingChains.get(i);
			e.addAll(endingChains.entrySet().stream().filter(f -> f.getKey()<i).flatMap(f -> f.getValue().stream()).collect(Collectors.toList()));
			if(e.size()>1) {
				Sequence newSeq = createSequence(s, e, i, index);
				if(checkThresholds(newSeq) && removeDuplicatesOf(sequences, newSeq)) 
					sequences.add(newSeq);
			}
		}
	}

	private Sequence createSequence(Sequence s, List<Integer> startIndices, int size, int index) {
		Location l = s.getSequence().get(index);
		Sequence newSeq = new Sequence();
		for(Integer i : startIndices) {
			Location l2 = new Location(l);
			newSeq.add(l2);
			List<Node> myNodes = l2.getContents().getNodes();
			for(int nodeIndex = myNodes.size()-1; nodeIndex>=0; nodeIndex--)
				if(nodeIndex<i || nodeIndex>i+size)
					myNodes.remove(nodeIndex);
			Range r = new Range(myNodes.get(0).getRange().get().begin, findNodeLocation(getStatementLoc(l2), myNodes.get(myNodes.size()-1)).getRange().end);
			l2.setRange(r);
			l2.getContents().setRange(r);
			l2.getContents().stripToRange();
			
		}
		return newSeq;
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
	
	private void sliceSequence(List<Sequence> sequences, Sequence s, Map<Integer, int[][]> statementEqualityArrays, int[] relevantLocationIndices) {
		List<WeightedPercentage> calcPercentages = getWeightedPercentages(s, statementEqualityArrays, relevantLocationIndices);
		List<WeightedPercentage> percentagesList = new ArrayList<>();
		for(int i = 0; i<calcPercentages.size(); i++) {
			percentagesList.add(calcPercentages.get(i));
			checkCloneValidity(sequences, s, relevantLocationIndices, calcPercentages, percentagesList, i);
		}
	}

	private void checkCloneValidity(List<Sequence> sequences, Sequence s, int[] relevantLocationIndices,
			List<WeightedPercentage> calcPercentages, List<WeightedPercentage> percentagesList, int i) {
		boolean notValidRegardingVariability = !checkType2VariabilityThreshold(calcAvg(percentagesList));
		if((notValidRegardingVariability && !canFixIt(calcPercentages, percentagesList, i)) || i+1 == calcPercentages.size()) {
			if(percentagesList.size()>1) {
				if(notValidRegardingVariability) percentagesList.remove(percentagesList.size()-1);
				Sequence newSeq = createSequence(s, calcPercentages.indexOf(percentagesList.get(0)), calcPercentages.indexOf(percentagesList.get(percentagesList.size()-1)), relevantLocationIndices);
				if(checkThresholds(newSeq) && removeDuplicatesOf(sequences, newSeq))
					sequences.add(newSeq);
			}
			percentagesList.clear();
		}
	}

	private Sequence createSequence(Sequence s, int from, int to, int[] relevantIndices) {
		Sequence newSeq = new Sequence();
		for(int locationIndex : relevantIndices) {
			Location l = s.getSequence().get(locationIndex);
			Location l2 = new Location(l);
			newSeq.add(l2);
			List<Node> myNodes = l2.getContents().getNodes();
			for(int nodeIndex = myNodes.size()-1; nodeIndex>=0; nodeIndex--)
				if(nodeIndex<from || nodeIndex>to)
					myNodes.remove(nodeIndex);
			Range r = new Range(myNodes.get(0).getRange().get().begin, findNodeLocation(getStatementLoc(l2), myNodes.get(myNodes.size()-1)).getRange().end);
			l2.setRange(r);
			l2.getContents().setRange(r);
			l2.getContents().stripToRange();
		}
		return newSeq;
	}
	
	public Location getStatementLoc(Location l) {
		if(l.getNext() != null)
			return l.getNext().getPrev();
		return l.getPrev().getNext();
	}
	
	private Location findNodeLocation(Location l, Node n) {
		if(l.getContents().getNodes().get(0) == n)
			return l;
		return findNodeLocation(l.getNext(), n);
	}

	private List<WeightedPercentage> getWeightedPercentages(Sequence s, Map<Integer, int[][]> statementEqualityArrays, int[] relevantLocationIndices) {
		List<WeightedPercentage> calcPercentages = new ArrayList<>();
		for(int currNodeIndex = 0; currNodeIndex<s.getAny().getContents().getNodes().size(); currNodeIndex++) {
			int[][] equality = statementEqualityArrays.get(currNodeIndex);
			calcPercentages.add(new WeightedPercentage(diffPerc(equality, relevantLocationIndices), equality[0].length));
		}
		return calcPercentages;
	}

	private boolean canFixIt(List<WeightedPercentage> calcPercentages, List<WeightedPercentage> percentagesList, int i) {
		percentagesList = new ArrayList<>(percentagesList);
		for(i++; i<calcPercentages.size(); i++) {
			percentagesList.add(calcPercentages.get(i));
			if(checkType2VariabilityThreshold(calcAvg(percentagesList))){
				return true;
			}
		}
		return false;
	}

	//Creates a per statement equality array.
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
		return getLocation(l.getNext(), node-1);
	}

	private boolean globalThresholdsMet(int[][] equalityArray, int total) {
		return checkType2VariabilityThreshold(diffPerc(equalityArray));
	}

	private int[][] createEqualityArray(List<List<Compare>> literals) {
		int[][] equalityArray = new int[literals.size()][literals.get(0).size()];
		final List<Compare> differentCompareLiterals = new ArrayList<>();
		int curr = 2;
		for(int j = 0; j<literals.get(0).size(); j++) {
			for(int i = 0; i<literals.size(); i++) {
				int index = differentCompareLiterals.indexOf(literals.get(i).get(j));
				if(index == -1) {
					equalityArray[i][j] = literals.get(i).get(j).doesType2Compare() ? curr++ : -curr++;
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
			List<Compare> literals2 = l.getContents().getCompare();
			literals.add(literals2);
			literals2.stream().filter(Compare::doesType2Compare).forEach(e -> e.setCloneType(CloneType.TYPE1));
		}
		return literals;
	}
}
