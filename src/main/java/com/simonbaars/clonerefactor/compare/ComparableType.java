package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

public class ComparableType implements Compare {
	ResolvedReferenceType type;
	
	public ComparableType(ReferenceType t) {
		type = (ResolvedReferenceType)t.resolve();
	}
	
	public boolean equals(Object o) {
		ResolvedReferenceType otherType = ((ComparableType)o).type;
		return type.equals(otherType);
	}
}
