package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ReferenceType;

public interface Compare {
	public boolean isValid();
	
	public int getHashCode();
	
	public static Compare create(Node node, CloneType type) {
		Compare c = null;
		if(node instanceof ReferenceType)
			c = new CompareType((ReferenceType)node);
		else if(node instanceof NameExpr)
			c = new CompareVariable((NameExpr)node);
		else if(node instanceof LiteralExpr)
			c = new CompareLiteral(type);
		if(c!=null && c.isValid())
			return c;
		return new CompareToken(node);
	}

	public default boolean compare(Compare c, CloneType type) {
		if(this.getClass() != c.getClass())
			return false;
		else return this.equals(c);
	}
}
