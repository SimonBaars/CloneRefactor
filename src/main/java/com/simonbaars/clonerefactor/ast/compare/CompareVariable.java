package com.simonbaars.clonerefactor.ast.compare;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareVariable extends Compare implements ResolvesSymbols {
	private final NameExpr variableName;
	private final ResolvedType type;
	
	public CompareVariable(NameExpr t) {
		super(t.getRange().get());
		variableName = t;
		ResolvedType resolvedType = null;
		try {
			type = t.calculateResolvedType();
		} catch (Exception e) {
			type = null;
		}
		//type = resolvedType;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!super.equals(o))
			return false;
		CompareVariable compareDec = ((CompareVariable)o);
		if(getCloneType() == CloneType.TYPE1 && !variableName.equals(compareDec.variableName))
			return false;
		return type == null || type.equals(compareDec.type);
	}

	@Override
	public int hashCode() {
		return (getCloneType() == CloneType.TYPE1 ? variableName.hashCode() : 0) + (type == null ? -3 : type.hashCode());
	}

	@Override
	public String toString() {
		return "CompareVariable [dec=" + variableName + ", type=" + type + "]";
	}
	
	@Override
	public boolean doesType2Compare() {
		return true;
	}
}
