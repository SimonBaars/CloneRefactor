package com.simonbaars.clonerefactor.ast.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.JavaToken;
import com.github.javaparser.JavaToken.Category;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.simonbaars.clonerefactor.ast.interfaces.HasCompareList;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.settings.CloneType;

public class CompareMethodCall extends Compare implements ResolvesSymbols {
	private final List<JavaToken> methodCall;
	private final MethodCallExpr methodCallExpr;
	private final Optional<MethodDeclarationProxy> type;
	private final List<Object> estimatedTypes = new ArrayList<>();
	
	public CompareMethodCall(MethodCallExpr t) {
		super(t.getRange().get());
		this.methodCall = getPartsOfCall(t);
		this.methodCallExpr = t;
		type = resolve(() -> new MethodDeclarationProxy(t.resolve()));
		if(!type.isPresent())
			estimateTypes(t);
	}

	// I'm not so sure about this whole estimateTypes thing. The problem is that JavaParser cannot resolve everything. In essence, we cannot guarantee equality, thus this can result in invalid refactorings. Because of that, we *should* remove this estimateTypes thing, and just mark the equality `false` for all unresolved method calls.
	private void estimateTypes(MethodCallExpr t) {
		estimatedTypes.addAll(t.getArguments().stream().map(e -> resolve(e::calculateResolvedType)).collect(Collectors.toList()));
	}
	
	@Override
	public boolean equals(Object c) {
		if(!super.equals(c))
			return false;
		CompareMethodCall other = (CompareMethodCall)c;
		if(getCloneType() == CloneType.TYPE1 && !methodCall.equals(other.methodCall))
			return false;
		if(type.isPresent() && other.type.isPresent())
			return getCloneType().isNotTypeOne() ? type.get().equalsType2(other.type.get()) : type.get().equalsType1(other.type.get());
		return estimatedTypes.equals(other.estimatedTypes);
	}

	private List<JavaToken> getPartsOfCall(MethodCallExpr methodCall2) {
		List<JavaToken> tokens = new ArrayList<>();
		for(JavaToken token : methodCall2.getTokenRange().get()) {
			if(token.toString().equals("("))
				return tokens;
			tokens.add(token);
		}
		return tokens;
	}

	@Override
	public int hashCode() {
		if(type.isPresent()) {
			if(getCloneType().isNotTypeOne())
				return type.get().hashcodeType2();
			return type.get().hashcodeType1();
		}
		return estimatedTypes.hashCode();
	}

	@Override
	public String toString() {
		return "CompareMethodCall [type=" + type + "]";
	}
	
	@Override
	public List<Compare> relevantChildren(Node statement, HasCompareList c){
		return c.getNodesForCompare(methodCallExpr.getArguments(), methodCallExpr.getRange().get()).values().stream().map(e -> Compare.create(statement, e, e.getTokenRange().get().getBegin(), getCloneType())).collect(Collectors.toList());
	}
	
	@Override
	public boolean doesType2Compare() {
		return true;
	}
}
