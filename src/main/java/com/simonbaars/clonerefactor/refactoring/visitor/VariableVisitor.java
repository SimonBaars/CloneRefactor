package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.Optional;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;

public class VariableVisitor extends VoidVisitorAdapter<Void> implements ResolvesSymbols {

	@Override
	public void visit(MethodCallExpr md, Void arg) {
		super.visit(md, arg);
		Optional<ResolvedMethodDeclaration> mr = resolve(md::resolve);
		if(mr.isPresent()) {
			System.out.println(mr.get().getQualifiedSignature());
		}
	}
}