package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.List;

public class Type2Sequence {
	private final List<Type2Statement> statements;

	public Type2Sequence() {
		statements = new ArrayList<>();
	}
	
	public Type2Sequence(List<Type2Statement> statementsWithinThreshold) {
		statements = statementsWithinThreshold;
	}

	public List<Type2Statement> getSequence() {
		return statements;
	}
	
	public int size() {
		return statements.size();
	}
	
	public void tryToExpand() {
		tryToExpandLeft();
	}

	private void tryToExpandLeft() {
		for(Type2Statement.)
	}
}
