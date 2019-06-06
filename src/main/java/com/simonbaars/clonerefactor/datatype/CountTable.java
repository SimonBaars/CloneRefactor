package com.simonbaars.clonerefactor.datatype;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CountTable<K> extends ListMap<K, Integer> {
	private int currentSize = 0;

	private static final long serialVersionUID = 1L;
	private String tableName;
	private final List<String> columns = new ArrayList<>();

	public CountTable(String tableName) {
		this.tableName = tableName;
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
	
	public void add(String columnName, CountMap<K> countMap) {
		entrySet().stream().filter(e -> !countMap.containsKey(e.getKey())).forEach(e -> e.getValue().add(0));
		countMap.entrySet().forEach(e -> {
			if(!containsKey(e.getKey()))
				IntStream.range(0, currentSize).forEach(i -> get(e.getKey()).add(0));
			get(e.getKey()).add(e.getValue());
		});
		columns.add(columnName);
		currentSize++;
    }
	
	@Override
	public String toString() {
		String columns = this.columns.stream().collect(Collectors.joining("\t", tableName+"\t", System.lineSeparator()));
		return columns+keySet().stream().sorted().map(e -> 
			e + "\t"+get(e).stream().map(f -> f.toString()).collect(Collectors.joining("\t"))
		).collect(Collectors.joining(System.lineSeparator()));
	}
}
