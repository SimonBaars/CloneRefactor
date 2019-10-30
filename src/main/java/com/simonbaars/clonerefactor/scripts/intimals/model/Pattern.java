package com.simonbaars.clonerefactor.scripts.intimals.model;

import java.util.List;

public class Pattern {
	private int id;
	private List<Match> matches;
	
	public Pattern(int id, List<Match> matches) {
		super();
		this.id = id;
		this.matches = matches;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<Match> getMatches() {
		return matches;
	}

	public void setMatches(List<Match> matches) {
		this.matches = matches;
	}
}
