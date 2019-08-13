package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.simonbaars.clonerefactor.context.context.interfaces.RequiresNodeContext;

public class ReturnVariablesCollector extends VoidVisitorAdapter<Map<SimpleName, Type>> implements RequiresNodeContext{
	
	private final List<Node> topLevel;

	public ReturnVariablesCollector(List<Node> topLevel) {
		this.topLevel = topLevel;
	}
	
    @Override
    public void visit(final AssignExpr n, final Map<SimpleName, Type> var) {
    	super.visit(n, var);
    	if(n.getTarget().isNameExpr())
    		var.put(((NameExpr)n.getTarget()).getName(), null);
    }
    
    @Override
    public void visit(final VariableDeclarator n, final Map<SimpleName, Type> var) {
    	super.visit(n, var);
    	Optional<Statement> parentStatement = getParentStatement(n);
    	if(parentStatement.isPresent() && topLevel.contains(parentStatement.get()))
    		var.put(n.getName(), n.getType());
    }
}