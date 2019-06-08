package com.simonbaars.clonerefactor.ast.compare;

import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareOutOfScope extends Compare {
	
	private static int x = Integer.MIN_VALUE;
	
	public CompareOutOfScope(Range range) {
		super(CloneType.TYPE1, range);
	}

	@Override
	public boolean equals(Object o) {
		return false; // We count out of scope as never being a clone.
	}
	
	@Override
	public int hashCode() {
		return x++; // Keep changing the hashcode so they will always differ on comparison.
	}

	@Override
	public String toString() {
		return "CompareFalse";
	}
}
