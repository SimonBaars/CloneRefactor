package com.simonbaars.clonerefactor.refactoring.visitor;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class DeclaresVariableVisitor extends GenericVisitorAdapter<Boolean, SimpleName> {
    @Override
    public Boolean visit(final VariableDeclarator n, final SimpleName var) {
    	super.visit(n, var);
  
    	if(n.getName().equals(var))
        	return true;
        return null;
    }
}