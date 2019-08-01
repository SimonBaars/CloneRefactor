package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.List;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class AssignmentsCollector extends VoidVisitorAdapter<List<AssignExpr>> {
    @Override
    public void visit(final AssignExpr n, final List<AssignExpr> var) {
    	super.visit(n, var);
    	var.add(n);
    }
}