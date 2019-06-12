package com.simonbaars.clonerefactor.ast.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.simonbaars.clonerefactor.ast.interfaces.HasCompareList;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.model.FiltersTokens;

public class CompareMethodCall extends Compare implements FiltersTokens, ResolvesSymbols {
	private final MethodCallExpr methodCall;
	private final Optional<ResolvedMethodDeclaration> type;
	private final List<Object> estimatedTypes = new ArrayList<>();
	
	public CompareMethodCall(MethodCallExpr t) {
		super(t.getRange().get());
		methodCall = t;
		type = resolve(() -> t.resolve());
		if(!type.isPresent())
			estimateTypes(t);
	}

	// I'm not so sure about this whole estimateTypes thing. The problem is that JavaParser cannot resolve everything. In essence, we cannot guarantee equality, thus this can result in invalid refactorings. Because of that, we *should* remove this estimateTypes thing, and just mark the equality `false` for all null types.
	private void estimateTypes(MethodCallExpr t) {
		estimatedTypes.addAll(t.getArguments().stream().map(e -> resolve(() -> e.calculateResolvedType())).collect(Collectors.toList()));
	}
	
	public boolean equals(Object c) {
		if(!super.equals(c))
			return false;
		CompareMethodCall other = (CompareMethodCall)c;
		if(type.isPresent() && other.type.isPresent()) {
			String methodSignature = type.get().getQualifiedSignature();
			String compareMethodSignature = other.type.get().getQualifiedSignature();
			if(getCloneType().isNotTypeOne()) 
				return getOnlyArguments(methodSignature).equals(getOnlyArguments(compareMethodSignature));
			return methodSignature.equals(compareMethodSignature);
		}
		return estimatedTypes.equals(other.estimatedTypes);
	}
	
	private String getOnlyArguments(String methodSignature) {
		return methodSignature.substring(methodSignature.indexOf('('));
	}

	@Override
	public int hashCode() {
		if(type.isPresent()) {
			String methodSignature = type.get().getQualifiedSignature();
			if(getCloneType().isNotTypeOne())
				return getOnlyArguments(methodSignature).hashCode();
			return methodSignature.hashCode();
		}
		return estimatedTypes.hashCode();
	}

	@Override
	public String toString() {
		return "CompareMethodCall [type=" + type + "]";
	}
	
	@Override
	public List<Compare> relevantChildren(Node statement, HasCompareList c){
		return c.getNodesForCompare(methodCall.getArguments(), methodCall.getRange().get()).values().stream().map(e -> Compare.create(statement, e, e.getTokenRange().get().getBegin(), getCloneType())).collect(Collectors.toList());
	}
	
	@Override
	public boolean doesType2Compare() {
		return true;
	}
}
