package com.simonbaars.clonerefactor.detection.type2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.Range;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.PrimitiveType;
import com.simonbaars.clonerefactor.detection.model.location.Location;

public class Type2RLocation extends Location {
	private final Set<Expression> diffExpressions = new HashSet<>();

	public Type2RLocation(Location clonedLocation, Range r) {
		super(clonedLocation, r);
	}

	public Type2RLocation(Location clonedLocation) {
		super(clonedLocation);
	}

	public Set<Expression> getDiffExpressions() {
		return diffExpressions;
	}
	
	public void addDummyParameters(MethodDeclaration extractedMethod) {
		getDummyParameters().stream().filter(p -> extractedMethod.getParameters().stream().noneMatch(a -> a.getNameAsString().equals(p))).forEach(p -> extractedMethod.addParameter(PrimitiveType.intType(), p));
	}
	
	public void addDummyParameters(MethodCallExpr expr) {
		getDummyParameters().stream().filter(p -> expr.getArguments().stream().noneMatch(a -> a.toString().equals(p))).forEach(p -> expr.addArgument(new NameExpr(p)));
	}
	
	private List<String> getDummyParameters() {
		int x = 1;
		List<String> params = new ArrayList<String>();
		for(Expression diff : diffExpressions) {
			if(diff instanceof MethodCallExpr || diff instanceof LiteralExpr) {
				params.add("dummy"+(x++));
			} else {
				params.add(((NameExpr)diff).getNameAsString());
			}
		}
		return params;
	}
}
