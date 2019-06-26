package com.simonbaars.clonerefactor.datatype.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CountTable extends ListMap<String, String> {
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

	public CountTable(Map<String, List<String>> m) {
		super(m);
	}
	
	public void add(String columnName, Map<? extends Object, ? extends Object> countMap) {
		entrySet().stream().filter(e -> !countMap.containsKey(e.getKey())).forEach(e -> e.getValue().add("0"));
		countMap.entrySet().forEach(e -> {
			if(!containsKey(e.getKey()))
				IntStream.range(0, currentSize).forEach(i -> get(e.getKey().toString()).add("0"));
			get(e.getKey().toString()).add(e.getValue().toString());
		});
		columns.add(columnName);
		currentSize++;
    }
	
	@Override
	public String toString() {
		String columnNames = this.columns.stream().collect(Collectors.joining("\t", tableName+"\t", System.lineSeparator()));
		return columnNames+keySet().stream().sorted().map(e -> 
			e + "\t"+get(e).stream().map(String::toString).collect(Collectors.joining("\t"))
		).collect(Collectors.joining(System.lineSeparator()));
	}
}
