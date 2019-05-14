package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;

public class CompareVariable extends Compare {
	private final NameExpr variableName;
	private final ResolvedValueDeclaration dec;
	private final ResolvedType type;
	
	public CompareVariable(CloneType cloneType, NameExpr t) {
		super(cloneType);
		variableName = t;
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
		if(!super.equals(o))
			return false;
		CompareVariable compareDec = ((CompareVariable)o);
		if(cloneType == CloneType.TYPE1 && !dec.getName().equals(compareDec.dec.getName()))
			return false;
		if(type == null) 
			return variableName.equals(compareDec.variableName);
		return type.equals(compareDec.type);
	}

	@Override
	public int getHashCode() {
		if(type!=null) return cloneType.isNotTypeOne() ? type.hashCode() : type.hashCode() + dec.getName().hashCode();
		return cloneType.isNotTypeOne() ? -3 : dec.getName().hashCode();
	}

	@Override
	public String toString() {
		return "CompareVariable [dec=" + dec.getName() + ", type=" + type + "]";
	}
}
