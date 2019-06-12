package com.simonbaars.clonerefactor.ast.compare;

import java.util.Optional;

import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;

public class CompareLiteral extends Compare implements ResolvesSymbols {
	
	private final LiteralExpr literal;
	private final Optional<ResolvedType> type;

	public CompareLiteral(LiteralExpr t) {
		super(t.getRange().get());
		this.literal=t;
		this.type = resolve(() -> t.calculateResolvedType());
	}
	
	@Override
	public boolean equals(Object o) {
		if(!super.equals(o))
			return false;
		CompareLiteral other = (CompareLiteral)o;
		if(getCloneType().isNotTypeOne()) {
			return type.equals(other.type);
		}
		return literal.equals(other.literal); 
	}

	@Override
	public int hashCode() {
		if(getCloneType().isNotTypeOne()) {
			return type.hashCode();
		}
		return literal.hashCode();
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
