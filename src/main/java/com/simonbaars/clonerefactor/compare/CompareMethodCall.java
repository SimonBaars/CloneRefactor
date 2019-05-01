package com.simonbaars.clonerefactor.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class CompareMethodCall extends Compare {
	private final ResolvedMethodDeclaration type;
	private final List<Object> estimatedTypes = new ArrayList<>();
	
	public CompareMethodCall(CloneType cloneType, MethodCallExpr t) {
		super(cloneType);
		ResolvedMethodDeclaration refType = null;
		try {
			refType = t.resolve();
		} catch (Exception e) {}
		type = refType;
		estimatedTypes.addAll(t.getArguments().stream().map(e -> {
			if(e instanceof NameExpr) 
				try {
					return ((NameExpr)e).resolve().getType();
				} catch (Exception ex) {}
			return e.getClass();
		}).collect(Collectors.toList()));
	}
	
	public boolean equals(Object c) {
		CompareMethodCall other = (CompareMethodCall)c;
		if(type!=null && other.type !=null)
			return type.getTypeParameters().equals(other.type.getTypeParameters());
		return estimatedTypes.equals(other.estimatedTypes);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public int getHashCode() {
		if(type!=null)
			return type.getTypeParameters().hashCode();
		return estimatedTypes.hashCode();
	}

	@Override
	public String toString() {
		return "CompareType [type=" + type + "]";
	}
}
