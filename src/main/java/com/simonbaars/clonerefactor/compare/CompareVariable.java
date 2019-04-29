package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;

public class CompareVariable implements Compare {
	private final ResolvedValueDeclaration dec;
	
	public CompareVariable(NameExpr t) {
		ResolvedValueDeclaration refType = null;
		try {
			refType = t.resolve();
		} catch (Exception e) {}
		dec = refType;
	}
	
	@Override
	public boolean compare(Compare o, CloneType type) {
		if(!Compare.super.compare(o, type))
			return false;
		ResolvedValueDeclaration compareDec = ((CompareVariable)o).dec;
		return type == CloneType.TYPE1 ? dec.equals(compareDec) : dec.getType().equals(compareDec.getType());
	}

	@Override
	public boolean isValid() {
		return dec!=null;
	}

	@Override
	public int getHashCode() {
		return dec.getType().hashCode();
	}
}
