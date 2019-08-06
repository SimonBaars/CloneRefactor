package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;

public class VariableVisitor extends VoidVisitorAdapter<Map<NameExpr, ResolvedType>> implements ResolvesSymbols {

	@Override
	public void visit(NameExpr ne, Map<NameExpr, ResolvedType> arg) {
		super.visit(ne, arg);
		Optional<ResolvedValueDeclaration> mr = resolve(ne::resolve);
		if(mr.isPresent()) {
			try { // GOES WRONG WAY TOO OFTEN! TODO TODO TODO	
				arg.put(ne, mr.get().getType());
			} catch (Exception e) {
				//e.printStackTrace(); TODO TODO
			}
		}
	}
	
	public ResolvedVariable findDeclaration(NameExpr variable) {
		if(variable.)
		Optional<VariableDeclarator> seekParents = seekParents(variable);
		return Optional.empty();
	}

	private Optional<? extends Node> seekParents(NameExpr variable) {
		if(variable.getParentNode().isPresent()) {
			Node parent = variable.getParentNode().get();
			if(parent instanceof MethodDeclaration) {
				Optional<Parameter> declaration = ((MethodDeclaration)parent).getParameters().stream().filter(parameter -> parameter.getName().equals(variable.getName())).findAny();
				if(declaration.isPresent())
					return declaration;
			}
			if(parent instanceof BlockStmt)
			for(Node child : parent.getChildNodes()) {
				
			}
		}
		return Optional.empty();
	}
}