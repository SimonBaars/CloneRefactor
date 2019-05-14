package com.simonbaars.clonerefactor.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.simonbaars.clonerefactor.model.FiltersTokens;
import com.simonbaars.clonerefactor.model.location.LocationContents;

public class CompareMethodCall extends Compare implements FiltersTokens {
	private final MethodCallExpr methodCall;
	private final ResolvedMethodDeclaration type;
	private final List<JavaToken> myTokens;
	private final List<Object> estimatedTypes = new ArrayList<>();
	
	public CompareMethodCall(CloneType cloneType, MethodCallExpr t) {
		super(cloneType);
		methodCall = t;
		ResolvedMethodDeclaration refType = null;
		try {
			refType = t.resolve();
		} catch (Exception e) {}
		type = refType;
		estimateTypes(t);
		myTokens = getEffectiveTokenList(t.getTokenRange().get());
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
		if(!super.equals(c))
			return false;
		CompareMethodCall other = (CompareMethodCall)c;
		if(cloneType == CloneType.TYPE1 && !myTokens.equals(other.myTokens))
			return false;
		if(type!=null && other.type !=null) {
			String methodSignature = type.getQualifiedSignature();
			String compareMethodSignature = other.type.getQualifiedSignature();
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
	public int getHashCode() {
		if(cloneType == CloneType.TYPE1)
			return myTokens.hashCode();
		if(type!=null)
			return type.getTypeParameters().hashCode();
		return estimatedTypes.hashCode();
	}

	@Override
	public String toString() {
		return "CompareMethodCall [type=" + type + "]";
	}
	
	@Override
	public List<Compare> relevantChildren(LocationContents c){
		return c.getNodesForCompare(methodCall.getArguments()).values().stream().map(e -> Compare.create(e, e.getTokenRange().get().getBegin(), cloneType)).collect(Collectors.toList());
	}
}
