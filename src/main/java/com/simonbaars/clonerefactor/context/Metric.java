package com.simonbaars.clonerefactor.context;

public enum Metric {
	LINES ("Lines"), NODES("Nodes"), TOKENS("Tokens");
	
	private String name;
	
	private Metric(String name) {
		this.name=name;
	}
	
	public String toString() {
		return name;
	}
}
