package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ReferenceType;

public interface Compare {
	public boolean isValid();
	
	public int getHashCode();
	
	public static Compare create(Object tokenOrNode, JavaToken e, CloneType type) {
		Compare c = null;
		if(tokenOrNode instanceof ReferenceType)
			c = new CompareType((ReferenceType)tokenOrNode);
		else if(tokenOrNode instanceof NameExpr)
			c = new CompareVariable((NameExpr)tokenOrNode);
		else if(tokenOrNode instanceof LiteralExpr)
			c = new CompareLiteral(type);
		if(c!=null && c.isValid())
			return c;
		return new CompareToken(e);
	}

	public default boolean compare(Compare c, CloneType type) {
		if(this.getClass() != c.getClass())
			return false;
		else return this.equals(c);
	}
}
