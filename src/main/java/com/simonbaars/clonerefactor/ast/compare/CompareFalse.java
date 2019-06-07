package com.simonbaars.clonerefactor.ast.compare;

import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareFalse extends Compare {
	
	private static int x = Integer.MIN_VALUE;
	
	public CompareFalse(Range range) {
		super(CloneType.TYPE1, range);
	}

	@Override
	public boolean equals(Object o) {
		return false; // Whatever clones we'll find, I won't be a part of it.
	}
	
	@Override
	public int hashCode() {
		return x++;
	}

	@Override
	public String toString() {
		return "CompareFalse";
	}
}
