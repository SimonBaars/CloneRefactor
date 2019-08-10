package com.simonbaars.clonerefactor.settings.progress;

public enum Stage {
	BUILDAST("Building AST"),
	BUILDLINEREGISTY("Building Clone Graph"),
	DETECTCLONES("Detecting Clones"),
	MAPCONTEXT("Mapping Context"),
	REFACTORCLONES("Refactoring Clones");
	
	private String name;
	
	private Stage(String name) {
		this.name=name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
