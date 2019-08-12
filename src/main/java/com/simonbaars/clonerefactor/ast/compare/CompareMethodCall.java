package com.simonbaars.clonerefactor.ast.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.JavaToken;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.simonbaars.clonerefactor.ast.interfaces.HasCompareList;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;

public class CompareMethodCall extends Compare implements ResolvesSymbols {
	private final List<JavaToken> methodCall;
	private final MethodCallExpr methodCallExpr;
	
	public CompareMethodCall(MethodCallExpr t) {
		super(t.getRange().get());
		this.methodCall = getPartsOfCall(t);
		this.methodCallExpr = t;
	}
	
	@Override
	public boolean equals(Object c) {
		if(!super.equals(c))
			return false;
		CompareMethodCall other = (CompareMethodCall)c;
		return methodCall.equals(other.methodCall);
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
		return methodCall.hashCode();
	}

	@Override
	public String toString() {
		return "CompareMethodCall [type=" + methodCallExpr + "]";
	}
	
	@Override
	public List<Compare> relevantChildren(Node statement, HasCompareList c){
		return c.getNodesForCompare(methodCallExpr.getArguments(), methodCallExpr.getRange().get()).values().stream().map(e -> Compare.create(statement, e, e.getTokenRange().get().getBegin(), getCloneType())).collect(Collectors.toList());
	}
	
	@Override
	public boolean doesType2Compare() {
		return true;
	}
	
	@Override
	public Expression getExpression(){
		return methodCallExpr;
	}
}
