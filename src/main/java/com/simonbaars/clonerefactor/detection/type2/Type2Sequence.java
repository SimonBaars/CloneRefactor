package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.List;

public class Type2Sequence {
	private final List<Type2Location> statements;

	public Type2Sequence() {
		statements = new ArrayList<>();
	}
	
	public Type2Sequence(List<Type2Location> statementsWithinThreshold) {
		statements = statementsWithinThreshold;
	}

	public List<Type2Location> getSequence() {
		return statements;
	}
	
	public int size() {
		return statements.size();
	}
	
	public void tryToExpand() {
		while(tryToExpandLeft() || tryToExpandRight());
	}

	private boolean tryToExpandRight() {
		return false;
	}

	private boolean tryToExpandLeft() {
		List<Type2Location> prevs = new ArrayList<>();
		for(Type2Location location : statements) {
			if(location.getPrev() == null)
				return false;
			prevs.add(location.getPrev());
		}
		Type2Location firstPrev = prevs.get(0);
		return false;
	}
}
