package com.simonbaars.clonerefactor.ast.resolution;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.refactoring.visitor.ResolvedVariable;
import com.simonbaars.clonerefactor.refactoring.visitor.ResolvedVariable.VariableType;

public class ResolveVariable implements ResolvesSymbols {
	
	private final Map<String, ClassOrInterfaceDeclaration> classes;
	private final SimpleName variable;

	public ResolveVariable (Map<String, ClassOrInterfaceDeclaration> classes, SimpleName variable) {
		this.classes = classes;
		this.variable = variable;
	}
	
	public Optional<ResolvedVariable> findDeclaration() {
		if(variable.getParentNode().isPresent() && variable.getParentNode().get() instanceof FieldAccessExpr) {
			FieldAccessExpr fieldAccess = (FieldAccessExpr)variable.getParentNode().get();
			if(fieldAccess.getScope() instanceof ThisExpr && getDeclaratorOfVariableForClass(getClass())) {
				
			}
		}
		Optional<ResolvedVariable> seekParents = seekParents(variable);
		return seekParents;
	}

	private Optional<ResolvedVariable> seekParents(Node node) {
		if(node.getParentNode().isPresent()) {
			Node parent = node.getParentNode().get();
			if(parent instanceof MethodDeclaration) {
				Optional<Parameter> declaration = ((MethodDeclaration)parent).getParameters().stream().filter(parameter -> parameter.getName().equals(variable)).findAny();
				if(declaration.isPresent())
					return createResolvedVariable(declaration.get()); 
			}
			else if(parent instanceof BlockStmt) {
				for(Node child : parent.getChildNodes()) {
					if(child instanceof ExpressionStmt && ((ExpressionStmt)child).getExpression() instanceof VariableDeclarationExpr) {
						VariableDeclarationExpr dec = (VariableDeclarationExpr)((ExpressionStmt)child).getExpression();
						for(VariableDeclarator decl : dec.getVariables()) {
							if(decl.getName().equals(variable))
								return createResolvedVariable(decl); 
						}
					}
				}
			}
			else if(parent instanceof ClassOrInterfaceDeclaration && !((ClassOrInterfaceDeclaration)parent).isInterface()) {
				Optional<ResolvedVariable> resolved = findVariableInSuperclass((ClassOrInterfaceDeclaration)parent);
				if(resolved.isPresent())
					return resolved;
			}
			seekParents(parent);
		}
		return Optional.empty();
	}
	
	private Optional<ResolvedVariable> findVariableInSuperclass(ClassOrInterfaceDeclaration classDecl){
		Optional<VariableDeclarator> declaration = getDeclaratorOfVariableForClass(classDecl);
		if(declaration.isPresent()) 
			return createResolvedVariable(declaration.get());
		return classDecl.getExtendedTypes().stream().map(e -> resolve(e::resolve)).filter(Optional::isPresent).map(Optional::get).map(ResolvedReferenceType::getQualifiedName)
				.filter(e -> classes.containsKey(e)).map(classes::get).map(this::findVariableInSuperclass).filter(Optional::isPresent).map(Optional::get).findAny();
	}

	private Optional<VariableDeclarator> getDeclaratorOfVariableForClass(ClassOrInterfaceDeclaration classDecl) {
		return classDecl.getMembers().stream().filter(e -> e instanceof FieldDeclaration).flatMap(e -> ((FieldDeclaration)e).getVariables().stream()).filter(e -> e.getName().equals(variable)).findAny();
	}

	private<T extends Node & NodeWithType & NodeWithSimpleName> Optional<ResolvedVariable> createResolvedVariable(T decl) {
		return Optional.of(new ResolvedVariable(decl.getType(), decl.getName(), decl, VariableType.METHODPARAMETER));
	}
}
