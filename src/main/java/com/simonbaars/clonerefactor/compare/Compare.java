package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ReferenceType;

public abstract class Compare {
	protected final CloneType cloneType;
	
	protected Compare(CloneType cloneType) {
		this.cloneType=cloneType;
	}
	
	public abstract boolean isValid();
	
	public abstract int getHashCode();
	
	public static Compare create(Object tokenOrNode, JavaToken e, CloneType cloneType) {
		Compare c = null;
		if(tokenOrNode instanceof ReferenceType)
			c = new CompareType(cloneType, (ReferenceType)tokenOrNode);
		else if(tokenOrNode instanceof NameExpr)
			c = new CompareVariable(cloneType, (NameExpr)tokenOrNode);
		else if(tokenOrNode instanceof LiteralExpr)
			c = new CompareLiteral(cloneType);
		else if(tokenOrNode instanceof SimpleName)
			c = new CompareName(cloneType);
		else if(tokenOrNode instanceof MethodCallExpr)
			c = new CompareMethodCall(cloneType, (MethodCallExpr)tokenOrNode);
		if(c!=null && c.isValid())
			return c;
		return new CompareToken(cloneType, e);
	}

	public boolean compare(Compare c) {
		if(this.getClass() != c.getClass())
			return false;
		else return this.equals(c);
	}
}
