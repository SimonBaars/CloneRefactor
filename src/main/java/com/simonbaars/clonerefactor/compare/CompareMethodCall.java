package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class CompareMethodCall extends Compare {
	private final ResolvedMethodDeclaration type;
	private final MethodCallExpr call;
	
	public CompareMethodCall(CloneType cloneType, MethodCallExpr t) {
		super(cloneType);
		ResolvedMethodDeclaration refType = null;
		try {
			refType = t.resolve();
		} catch (Exception e) {}
		t.getArguments().parallelStream().forEach(e -> System.out.println(e.getClass()));
		type = refType;
		call = t;
	}
	
	public boolean equals(Object c) {
		CompareMethodCall other = (CompareMethodCall)c;
		if(type!=null && other.type !=null) {
			if(cloneType.isNotTypeOne())
				return type.getTypeParameters().equals(other.type.getTypeParameters());
			return type.equals(other.type);
		}
		if(cloneType.isNotTypeOne())
			return call.getArguments().for.get().equals(other.call.getTypeArguments().get());
		return call.getTokenRange().get().equals(other.call.getTokenRange().get());
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public int getHashCode() {
		if(type!=null) {
			if(cloneType.isNotTypeOne())
				return type.getTypeParameters().hashCode();
			return type.hashCode();
		}
		if(cloneType.isNotTypeOne())
			return call.getTypeArguments().get().hashCode();
		return call.getTokenRange().get().hashCode();
	}

	@Override
	public String toString() {
		return "CompareType [type=" + type + "]";
	}
}
