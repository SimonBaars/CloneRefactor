package com.simonbaars.clonerefactor.datatype;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CountTable<K> extends ListMap<K, Integer> {
	private int currentSize = 0;

	private static final long serialVersionUID = 1L;

	public CountTable() {
	}

	public CountTable(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public CountTable(int initialCapacity) {
		super(initialCapacity);
	}

	public CountTable(Map<? extends K, List<Integer>> m) {
		super(m);
	}
	
	public void add(CountMap<K> countMap) {
		entrySet().stream().filter(e -> !countMap.containsKey(e.getKey())).forEach(e -> e.getValue().add(0));
		countMap.entrySet().forEach(e -> {
			if(!containsKey(e.getKey()))
				IntStream.range(0, currentSize).forEach(i -> get(e.getKey()).add(0));
			get(e.getKey()).add(e.getValue());
		});
		currentSize++;
    }
	
	@Override
	public String toString() {
		return keySet().stream().sorted().map(e -> e+"\t"+get(e).stream().map(f -> f.toString()).collect(Collectors.joining("\t"))).collect(Collectors.joining(System.lineSeparator()));
	}
}