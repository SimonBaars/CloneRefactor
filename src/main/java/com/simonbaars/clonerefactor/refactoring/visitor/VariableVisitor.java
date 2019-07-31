package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;

public class VariableVisitor extends VoidVisitorAdapter<Map<NameExpr, ResolvedType>> implements ResolvesSymbols {

	@Override
	public void visit(NameExpr ne, Map<NameExpr, ResolvedType> arg) {
		super.visit(ne, arg);
		Optional<ResolvedType> mr = resolve(ne::calculateResolvedType);
		if(mr.isPresent()) {
			arg.put(ne, mr.get());
		}
	}
}