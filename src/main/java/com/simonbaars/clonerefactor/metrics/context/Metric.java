package com.simonbaars.clonerefactor.metrics.context;

public enum Metric {
	LINES ("Lines"), EFFECTIVELINES("Effective Lines"), NODES("Nodes"), TOKENS("Tokens");
	
	private String name;
	
	private Metric(String name) {
		this.name=name;
	}
	
	public String toString() {
		return name;
	}
}
