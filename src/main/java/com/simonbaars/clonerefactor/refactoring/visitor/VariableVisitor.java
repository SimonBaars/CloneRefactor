package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.Map;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.simonbaars.clonerefactor.clonegraph.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.clonegraph.resolution.ResolveVariable;
import com.simonbaars.clonerefactor.clonegraph.resolution.ResolvedVariable;

public class VariableVisitor extends VoidVisitorAdapter<Map<SimpleName, ResolvedVariable>> implements ResolvesSymbols {

	private Map<String, ClassOrInterfaceDeclaration> classes;

	public VariableVisitor(Map<String, ClassOrInterfaceDeclaration> classes) {
		this.classes = classes;
	}

	@Override
	public void visit(SimpleName ne, Map<SimpleName, ResolvedVariable> arg) {
		super.visit(ne, arg);
		new ResolveVariable(classes, ne).findDeclaration().ifPresent(var -> arg.put(ne, var));
	}
}