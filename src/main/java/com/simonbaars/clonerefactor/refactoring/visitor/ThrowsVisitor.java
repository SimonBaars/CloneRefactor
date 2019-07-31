package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;

public class ThrowsVisitor extends VoidVisitorAdapter<MethodDeclaration> implements RequiresNodeContext, ResolvesSymbols {

	@Override
	public void visit(ThrowStmt t, MethodDeclaration extractedMethod) {
		super.visit(t, extractedMethod);
		if(!hasCatch(t))
			addThrowsToNodeList(extractedMethod, t);
	}
	
	private void addThrowsToNodeList(MethodDeclaration extractedMethod, Node n) {
		Expression expr = ((ThrowStmt)n).getExpression();
		Optional<ResolvedType> rt = resolve(() -> expr.calculateResolvedType());
		if(rt.isPresent() && rt.get().isReference()) {
			parseReferenceType(extractedMethod, rt);
		}
	}

	private void parseReferenceType(MethodDeclaration extractedMethod, Optional<ResolvedType> rt) {
		ResolvedReferenceType rrt = rt.get().asReferenceType();
		ParseResult<Type> pr = new JavaParser().parseType(rrt.getQualifiedName());
		if(pr.getResult().isPresent()) {
			Type t = pr.getResult().get();
			if(t.isReferenceType()) {
				extractedMethod.addThrownException((ReferenceType)t);
			}
		}
	}

	private boolean hasCatch(ThrowStmt n) {
		return getTryStatement(n).isPresent();
	}
}