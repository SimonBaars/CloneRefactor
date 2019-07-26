package com.simonbaars.clonerefactor.refactoring.populate;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.location.Location;

public class PopulateThrows implements RequiresNodeContext {

	private MethodDeclaration decl;

	public PopulateThrows(MethodDeclaration decl) {
		this.decl = decl;
	}

	public void execute(Location loc) {
		for(Node n : loc.getContents().getNodes()) {
			if(n instanceof ThrowStmt) {
				if(hasCatch((ThrowStmt)n)) {
					
				}
			}
		}
	}

	private boolean hasCatch(ThrowStmt n) {
		n.getExpression().calculateResolvedType();
		return false;
	}

}
