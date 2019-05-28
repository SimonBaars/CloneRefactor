package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.List;

public class Type2Sequence {
	private final List<List<Type2Statement>> statements = new ArrayList<>();

	public List<List<Type2Statement>> getStatements() {
		return statements;
	}
}
