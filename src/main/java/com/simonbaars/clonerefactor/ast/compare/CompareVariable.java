package com.simonbaars.clonerefactor.ast.compare;

import java.util.Optional;

import com.github.javaparser.ast.expr.NameExpr;
import com.simonbaars.clonerefactor.ast.ASTHolder;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.ast.resolution.ResolveVariable;
import com.simonbaars.clonerefactor.ast.resolution.ResolvedVariable;
import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareVariable extends Compare implements ResolvesSymbols {
	private final NameExpr variableName;
	private final Optional<ResolvedVariable> type;
	
	public CompareVariable(NameExpr t) {
		super(t.getRange().get());
		variableName = t;
		type = new ResolveVariable(ASTHolder.getClasses(), t.getName()).findDeclaration();
	}
	
	@Override
	public boolean equals(Object o) {
		if(!super.equals(o))
			return false;
		CompareVariable compareDec = ((CompareVariable)o);
		if(getCloneType() == CloneType.TYPE1R && !variableName.equals(compareDec.variableName))
			return false;
		return type.equals(compareDec.type);
	}

	@Override
	public int hashCode() {
		return (getCloneType() == CloneType.TYPE1R ? variableName.hashCode() : 0) + (type.isPresent() ? type.get().hashCode() : -3);
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
