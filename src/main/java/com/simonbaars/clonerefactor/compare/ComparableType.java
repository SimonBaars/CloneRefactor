package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

public class ComparableType implements Compare {
	private final ResolvedReferenceType type;
	
	public ComparableType(ReferenceType t) {
		ResolvedReferenceType refType = null;
		try {
			refType = (ResolvedReferenceType)t.resolve();
		} catch (Exception e) {}
		type = refType;
	}
	
	public boolean equals(Object o) {
		ResolvedReferenceType otherType = ((ComparableType)o).type;
		return type.equals(otherType);
	}

	@Override
	public boolean isValid() {
		return type!=null;
	}

	@Override
	public int getHashCode() {
		return type.hashCode();
	}
}
