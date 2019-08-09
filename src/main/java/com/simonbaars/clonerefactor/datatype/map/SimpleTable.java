package com.simonbaars.clonerefactor.datatype.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SimpleTable extends ArrayList<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1746137296809094943L;
	
	public SimpleTable(String...columns) {
		super(1);
		if(columns.length != 0)
			addRow((Object[])columns);
	}

	public void addRow(Object...objects) {
		add(Arrays.stream(objects).map(e -> e.toString()).collect(Collectors.joining("\t")));
	}
}
