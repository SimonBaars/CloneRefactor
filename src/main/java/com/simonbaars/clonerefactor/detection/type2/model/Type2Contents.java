package com.simonbaars.clonerefactor.detection.type2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Type2Contents {
	private final int[] contents;
	private final Map<Type2Contents, WeightedPercentage> equalityMap = new HashMap<>();
	private final List<Type2Location> statements = new ArrayList<>();
	
	public Type2Contents(int[] contents) {
		super();
		this.contents = contents;
	}

	public int[] getContents() {
		return contents;
	}

	public Map<Type2Contents, WeightedPercentage> getEqualityMap() {
		return equalityMap;
	}

	public List<Type2Location> getStatements() {
		return statements;
	}
	
	public List<Type2Location> getStatementsWithinThreshold(Predicate<Double> threshold) {
		List<Type2Location> statementsWithinThreshold = equalityMap.entrySet().stream().filter(e -> threshold.test(e.getValue().getPercentage())).flatMap(e -> e.getKey().getStatements().stream()).collect(Collectors.toList());
		statementsWithinThreshold.addAll(statements);
		return statementsWithinThreshold;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(contents);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Type2Contents other = (Type2Contents) obj;
		return Arrays.equals(contents, other.contents);
	}

	@Override
	public String toString() {
		return "Type2RLocation [contents=" + Arrays.toString(contents) + "]";
	}
}
