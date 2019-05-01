package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;

public class CompareVariable extends Compare {
	private final ResolvedValueDeclaration dec;
	private final ResolvedType type;
	
	public CompareVariable(CloneType cloneType, NameExpr t) {
		super(cloneType);
		ResolvedValueDeclaration refType = null;
		ResolvedType resolvedType = null;
		try {
			refType = t.resolve();
			resolvedType = refType.getType();
		} catch (Exception e) {}
		dec = refType;
		type = resolvedType;
	}
	
	@Override
	public boolean equals(Object o) {
		CompareVariable compareDec = ((CompareVariable)o);
		if(cloneType == CloneType.TYPE1 && !dec.getName().equals(compareDec.dec.getName())) {
			return false;
		}
		return (type == null && compareDec.type == null) || (type!=null && type.equals(compareDec.type));
	}

	@Override
	public boolean isValid() {
		return dec!=null;
	}

	@Override
	public int getHashCode() {
		if(type!=null) return cloneType.isNotTypeOne() ? type.hashCode() : type.hashCode() + dec.getName().hashCode();
		return -3;
	}

	@Override
	public String toString() {
		return "CompareVariable [dec=" + dec.getName() + ", type=" + type + "]";
	}
}
