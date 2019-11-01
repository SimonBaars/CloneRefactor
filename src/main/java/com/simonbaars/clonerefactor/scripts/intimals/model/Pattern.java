package com.simonbaars.clonerefactor.scripts.intimals.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pattern {
	private int id;
	private List<Match> matches;
	
	public Pattern(int id, List<Match> matches) {
		super();
		this.id = id;
		this.matches = matches;
	}

	public Pattern(int id2) {
		this.id=id2;
		this.matches = new ArrayList<>();
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

	@Override
	public String toString() {
		return "Pattern [id=" + id + ", matches=" + Arrays.toString(matches.toArray()) + "]";
	}
}
