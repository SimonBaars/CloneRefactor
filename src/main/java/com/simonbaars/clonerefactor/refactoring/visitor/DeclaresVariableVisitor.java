package com.simonbaars.clonerefactor.refactoring.visitor;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class DeclaresVariableVisitor extends GenericVisitorAdapter<Boolean, NameExpr> {
    @Override
    public Boolean visit(final VariableDeclarator n, final NameExpr var) {
    	super.visit(n, var);
  
    	if(n.getName().equals(var.getName()))
        	return true;
        return null;
    }
}