package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.simonbaars.clonerefactor.context.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.detection.model.Sequence;

public class PopulateReturningFlow implements PopulatesExtractedMethod, RequiresNodeContext {
	public PopulateReturningFlow() {}
	
	private boolean returns = false;
	
	@Override
	public void prePopulate(MethodDeclaration extractedMethod, List<Node> topLevel) {
		Node lastNode = topLevel.get(topLevel.size()-1);
		if(lastNode instanceof ReturnStmt) {
			Optional<MethodDeclaration> d = getMethod(lastNode);
			if(d.isPresent()) {
				extractedMethod.setType(d.get().getType());
				returns = true;
			}
		}
	}

	@Override
	public Optional<Statement> modifyMethodCall(Sequence s, MethodCallExpr expr) {
		if(returns)
			return Optional.of(new ReturnStmt(expr));
		return Optional.empty();
	}

	@Override
	public void postPopulate(Sequence s, MethodDeclaration extractedMethod) {
		returns = false;
	}

}
