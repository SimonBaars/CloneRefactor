package com.simonbaars.clonerefactor.refactoring.visitor;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class DeclaresVariableVisitor extends GenericVisitorAdapter<Boolean, NameExpr> {

	@Override
    public Boolean visit(final VariableDeclarationExpr n, final NameExpr var) {
        super.visit(n, var);
        if(declaresVariable(n, var))
        	return true;
        return null;
    }

	private boolean declaresVariable(final VariableDeclarationExpr n, final NameExpr var) {
		return n.getVariables().stream().anyMatch(e -> e.getName().equals(var.getName()));
	}

    @Override
    public Boolean visit(final VariableDeclarator n, final NameExpr var) {
    	super.visit(n, var);
  
    	if(n.getName().equals(var.getName()))
        	return true;
        return null;
    }
}