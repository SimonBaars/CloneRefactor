package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

public class CompareType extends Compare {
	private final ResolvedReferenceType type;
	
	public CompareType(CloneType cloneType, ReferenceType t) {
		super(cloneType);
		ResolvedReferenceType refType = null;
		try {
			refType = (ResolvedReferenceType)t.resolve();
		} catch (Exception e) {}
		type = refType;
	}
	
	public boolean equals(Object o) {
		return type.getQualifiedName().equals(((CompareType)o).type.getQualifiedName());
	}

	@Override
	public boolean isValid() {
		return type!=null;
	}

	@Override
	public int getHashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		return "CompareType [type=" + type + "]";
	}
}
