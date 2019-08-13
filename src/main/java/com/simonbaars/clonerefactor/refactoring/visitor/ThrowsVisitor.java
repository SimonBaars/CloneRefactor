package com.simonbaars.clonerefactor.refactoring.visitor;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.simonbaars.clonerefactor.clonegraph.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.context.context.interfaces.RequiresNodeContext;

public class ThrowsVisitor extends VoidVisitorAdapter<MethodDeclaration> implements RequiresNodeContext, ResolvesSymbols {

	@Override
	public void visit(ThrowStmt t, MethodDeclaration extractedMethod) {
		super.visit(t, extractedMethod);
		if(!hasCatch(t))
			addThrowsToNodeList(extractedMethod, t);
	}
	
	@SuppressWarnings("rawtypes")
	private void addThrowsToNodeList(MethodDeclaration extractedMethod, Node n) {
		Expression expr = ((ThrowStmt)n).getExpression();
		if(expr instanceof NodeWithType) {
			Type t = ((NodeWithType)expr).getType();
			if(t instanceof ReferenceType && !extractedMethod.getThrownExceptions().contains(t)) {
				extractedMethod.addThrownException((ReferenceType)t);
			}
		}
	}

	private boolean hasCatch(ThrowStmt n) {
		return getTryStatement(n).isPresent();
	}
}