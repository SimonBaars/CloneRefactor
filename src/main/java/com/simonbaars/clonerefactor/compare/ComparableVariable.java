package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;

public class ComparableVariable implements Compare {
	ResolvedValueDeclaration dec;
	
	public ComparableVariable(NameExpr t) {
		dec = t.resolve();
	}
	
	@Override
	public boolean compare(Compare o, int type) {
		if(!Compare.super.compare(o, type))
			return false;
		ResolvedValueDeclaration compareDec = ((ComparableVariable)o).dec;
		return type == 1 ? dec.equals(compareDec) : dec.getType().equals(compareDec.getType());
	}
}
