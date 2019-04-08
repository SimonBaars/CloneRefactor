package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.List;


public class CloneClass {
	private final List<LineTokens> cloneClass;

	public CloneClass(List<LineTokens> lines) {
		cloneClass = new ArrayList<>(lines);
	}

	public List<LineTokens> getCloneClass() {
		return cloneClass;
	}
	
	/*@Override
	public String toString() {
		return cloneClass.stream().map(e -> e.stream().map(f -> f.getTokens().toString()).collect(Collectors.joining(", "))).collect(Collectors.joining(" => "));
	}*/
}
