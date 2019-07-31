package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.Optional;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;

public class VariableVisitor extends VoidVisitorAdapter<Void> implements ResolvesSymbols {

	@Override
	public void visit(NameExpr ne, Void arg) {
		super.visit(ne, arg);
		Optional<ResolvedValueDeclaration> mr = resolve(ne::resolve);
		if(mr.isPresent()) {
			System.out.println(mr.get().);
		}
	}
}