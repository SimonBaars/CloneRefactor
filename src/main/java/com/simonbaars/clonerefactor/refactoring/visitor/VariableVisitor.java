package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;

public class VariableVisitor extends VoidVisitorAdapter<Map<NameExpr, ResolvedType>> implements ResolvesSymbols {

	@Override
	public void visit(NameExpr ne, Map<NameExpr, ResolvedType> arg) {
		super.visit(ne, arg);
		Optional<ResolvedValueDeclaration> mr = resolve(ne::resolve);
		if(mr.isPresent()) {
			try { // GOES WRONG WAY TOO OFTEN! TODO TODO TODO	
				arg.put(ne, mr.get().getType());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else System.out.println("not "+ne);
	}
}