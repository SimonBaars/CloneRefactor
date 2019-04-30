package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class CompareMethodCall implements Compare {
	private final ResolvedMethodDeclaration type;
	private final MethodCallExpr call;
	
	public CompareMethodCall(MethodCallExpr t) {
		ResolvedMethodDeclaration refType = null;
		try {
			refType = t.resolve();
		} catch (Exception e) {}
		type = refType;
		call = t;
	}
	
	@Override
	public boolean compare(Compare c, CloneType cloneType) {
		if(!Compare.super.compare(c, cloneType))
			return false;
		CompareMethodCall other = (CompareMethodCall)c;
		if(type!=null && other.type !=null) {
			if(cloneType.isNotTypeOne())
				return type.getTypeParameters().equals(other.type.getTypeParameters());
			return type.equals(other.type);
		}
		if(cloneType.isNotTypeOne())
			return call.getTypeArguments().get().equals(other.call.getTypeArguments());
		return call.getTokenRange().get().equals(other.call.getTokenRange().get());
	}
	
	public boolean equals(Object o) {
		return true;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public int getHashCode() {
		return type == null ? call.hashCode() : type.hashCode();
	}

	@Override
	public String toString() {
		return "CompareType [type=" + type + "]";
	}
}
