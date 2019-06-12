package com.simonbaars.clonerefactor.ast.compare;

import java.util.Optional;

import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;

public class CompareType extends Compare implements ResolvesSymbols {
	private final ReferenceType referenceType;
	private final Optional<ResolvedType> type;
	
	public CompareType(ReferenceType t) {
		super(t.getRange().get());
		this.referenceType = t;
		type = resolve(() -> t.resolve());
	}
	
	public boolean equals(Object o) {
		if(!super.equals(o))
			return false;
		CompareType other = ((CompareType)o);
		if(type.isPresent())
			return type.equals(other.type);
		return referenceType.equals(other.referenceType);
	}
	
	@Override
	public int hashCode() {
		if(type==null)
			return referenceType.hashCode();
		return type.hashCode();
	}

	@Override
	public String toString() {
		return "CompareType [type=" + type + "]";
	}
}
