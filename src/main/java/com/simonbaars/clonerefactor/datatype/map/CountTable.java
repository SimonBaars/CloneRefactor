package com.simonbaars.clonerefactor.datatype.map;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CountTable extends LinkedHashMap<String, Map<? extends Object, ? extends Object>> {

	private static final long serialVersionUID = 1L;
	private String tableName;

	public CountTable(String tableName) {
		this.tableName = tableName;
	}

	public CountTable(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public CountTable(int initialCapacity) {
		super(initialCapacity);
	}
	
	@Override
	public String toString() {
		String columnNames = this.keySet().stream().collect(Collectors.joining("\t", tableName+"\t", System.lineSeparator()));
		Set<? extends Object> rows = values().stream().flatMap(e -> e.keySet().stream()).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
		return columnNames + rows.stream().map(header -> 
			header + "\t" + values().stream().map(row -> 
				row.containsKey(header) ? row.get(header).toString() : "0").collect(Collectors.joining("\t"))
		).collect(Collectors.joining(System.lineSeparator()));
	}
}
