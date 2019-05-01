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
		estimateTypes(t);
	}

	/*
	 * I'm not so sure about this whole estimateTypes thing. The problem is that JavaParser cannot resolve everything. In essence, we cannot guarantee equality, thus this can result in invalid refactorings. Because of that, we *should* remove this estimateTypes thing, and just mark the equality `false` for all null types.
	 */
	private void estimateTypes(MethodCallExpr t) {
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
		if(type!=null && other.type !=null) {
			String methodSignature = type.getQualifiedSignature();
			String compareMethodSignature = other.type.getSignature();
			if(cloneType.isNotTypeOne()) 
				return getOnlyArguments(methodSignature).equals(getOnlyArguments(compareMethodSignature));
			return methodSignature.equals(compareMethodSignature);
		}
		return estimatedTypes.equals(other.estimatedTypes);
	}
	
	private String getOnlyArguments(String methodSignature) {
		return methodSignature.substring(methodSignature.indexOf('('));
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
