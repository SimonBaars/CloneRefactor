package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;

public class CompareVariable implements Compare {
	private final ResolvedValueDeclaration dec;
	private final ResolvedType type;
	
	public CompareVariable(NameExpr t) {
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
	public boolean compare(Compare o, CloneType cloneType) {
		if(!Compare.super.compare(o, cloneType))
			return false;
		CompareVariable compareDec = ((CompareVariable)o);
		if(cloneType == CloneType.TYPE1 && dec.getName()!=null && !dec.getName().equals(compareDec.dec.getName())) {
			return false;
		}
		return type == null || type.equals(compareDec.type);
	}
	
	@Override
	public boolean equals(Object o) {
		return true; //We compare using the compare method.
	}

	@Override
	public boolean isValid() {
		return dec!=null;
	}

	@Override
	public int getHashCode() {
		try {
			return type.hashCode();
		} catch(Exception e) {
			return -2;
		}
	}

	@Override
	public String toString() {
		return "CompareVariable [dec=" + dec.getName() + ", type=" + type + "]";
	}
}
