package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.refactoring.visitor.ResolvedVariable.VariableType;

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
	
	public Optional<ResolvedVariable> findDeclaration(NameExpr variable) {
		Optional<ResolvedVariable> seekParents = seekParents(variable);
		return seekParents;
	}

	private Optional<ResolvedVariable> seekParents(NameExpr variable) {
		if(variable.getParentNode().isPresent()) {
			Node parent = variable.getParentNode().get();
			if(parent instanceof MethodDeclaration) {
				Optional<Parameter> declaration = ((MethodDeclaration)parent).getParameters().stream().filter(parameter -> parameter.getName().equals(variable.getName())).findAny();
				if(declaration.isPresent())
					return createResolvedVariable(declaration.get()); 
			}
			if(parent instanceof BlockStmt) {
				for(Node child : parent.getChildNodes()) {
					if(child instanceof ExpressionStmt && ((ExpressionStmt)child).getExpression() instanceof VariableDeclarationExpr) {
						VariableDeclarationExpr dec = (VariableDeclarationExpr)((ExpressionStmt)child).getExpression();
						for(VariableDeclarator decl : dec.getVariables()) {
							if(decl.getName().equals(variable.getName()))
								return createResolvedVariable(decl); 
						}
					}
				}
			}
			if(parent instanceof ClassOrInterfaceDeclaration && !((ClassOrInterfaceDeclaration)parent).isInterface()) {
				ClassOrInterfaceDeclaration classDecl = (ClassOrInterfaceDeclaration)parent;
				Optional<VariableDeclarator> declaration = classDecl.getMembers().stream().filter(e -> e instanceof FieldDeclaration).flatMap(e -> ((FieldDeclaration)e).getVariables().stream()).filter(e -> e.getName().equals(variable.getName())).findAny();
				if(declaration.isPresent()) 
					return createResolvedVariable(declaration.get());
			}
		}
		return Optional.empty();
	}

	private<T extends Node & NodeWithType & NodeWithSimpleName> Optional<ResolvedVariable> createResolvedVariable(T decl) {
		return Optional.of(new ResolvedVariable(decl.getType(), decl.getName(), decl, VariableType.METHODPARAMETER));
	}
}