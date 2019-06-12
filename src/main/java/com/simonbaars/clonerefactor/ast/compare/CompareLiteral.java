package com.simonbaars.clonerefactor.ast.compare;

import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.resolution.types.ResolvedType;

public class CompareLiteral extends Compare {
	
	private final LiteralExpr literal;
	private ResolvedType type = null;

	public CompareLiteral(LiteralExpr t) {
		super(t.getRange().get());
		this.literal=t;
		try {
			this.type = t.calculateResolvedType();
		} catch (Exception e) {}
	}
	
	@Override
	public boolean equals(Object o) {
		if(!super.equals(o))
			return false;
		CompareLiteral other = (CompareLiteral)o;
		if(getCloneType().isNotTypeOne()) {
			return type!=null && type.equals(other.type);
		}
		return literal.equals(other.literal); 
	}

	@Override
	public int hashCode() {
		if(getCloneType().isNotTypeOne()) {
			return type!=null ? type.hashCode() : -1;
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
