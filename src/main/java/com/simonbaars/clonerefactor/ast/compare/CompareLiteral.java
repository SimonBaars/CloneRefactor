package com.simonbaars.clonerefactor.ast.compare;

import java.util.Optional;

import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareLiteral extends Compare implements ResolvesSymbols {
	
	private final LiteralExpr literal;
	private final Optional<ResolvedType> type;

	public CompareLiteral(LiteralExpr t, CloneType cloneType) {
		super(t.getRange().get());
		this.literal=t;
		if(cloneType.isNotType1())
			this.type = resolve(t::calculateResolvedType);
		else this.type = Optional.empty();
	}
	
	@Override
	public boolean equals(Object o) {
		if(!super.equals(o))
			return false;
		CompareLiteral other = (CompareLiteral)o;
		return getCloneType().isNotType1() ? type.equals(other.type) : literal.equals(other.literal); 
	}

	@Override
	public int hashCode() {
		return getCloneType().isNotType1() ? type.hashCode() : literal.hashCode();
	}
	
	@Override
	public String toString() {
		return "CompareLiteral [literal=" + literal + ", type=" + type + "]";
	}

	@Override
	public boolean doesType2Compare() {
		return true;
	}
}
