package com.simonbaars.clonerefactor.refactoring.populate;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class PopulateThrows implements PopulatesExtractedMethod, RequiresNodeContext, ResolvesSymbols {

	@Override
	public void execute(MethodDeclaration extractedMethod, Sequence sequence) {
		execute(extractedMethod, sequence.getAny());
	}
	
	public void execute(MethodDeclaration extractedMethod, Location loc) {
		List<Node> nodes = loc.getContents().getNodes();
		for(Node n : nodes) {
			if(n instanceof ThrowStmt && !hasCatch(nodes, (ThrowStmt)n)) {
				addThrowsToNodeList(extractedMethod, n);
			}
		}
	}

	private void addThrowsToNodeList(MethodDeclaration extractedMethod, Node n) {
		Expression expr = ((ThrowStmt)n).getExpression();
		Optional<ResolvedType> rt = resolve(() -> expr.calculateResolvedType());
		if(rt.isPresent() && rt.get().isReference()) {
			parseReferenceType(extractedMethod, rt);
		}
	}

	private void parseReferenceType(MethodDeclaration extractedMethod, Optional<ResolvedType> rt) {
		ResolvedReferenceType rrt = rt.get().asReferenceType();
		ParseResult<Type> pr = new JavaParser().parseType(rrt.getQualifiedName());
		if(pr.getResult().isPresent()) {
			Type t = pr.getResult().get();
			if(t.isReferenceType()) {
				extractedMethod.addThrownException((ReferenceType)t);
			}
		}
	}

	private boolean hasCatch(List<Node> nodes, ThrowStmt n) {
		Optional<TryStmt> tr = getTryStatement(n);
		if(!tr.isPresent())
			return false;
		return nodes.contains(tr.get());
	}

}
