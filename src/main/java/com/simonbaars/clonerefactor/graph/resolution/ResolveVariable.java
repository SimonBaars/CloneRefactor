package com.simonbaars.clonerefactor.graph.resolution;

import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.simonbaars.clonerefactor.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.context.relation.ResolvesFullyQualifiedIdentifiers;
import com.simonbaars.clonerefactor.graph.resolution.ResolvedVariable.VariableType;

public class ResolveVariable implements ResolvesFullyQualifiedIdentifiers, RequiresNodeContext {
	
	private final Map<String, ClassOrInterfaceDeclaration> classes;
	private final SimpleName variable;

	public ResolveVariable (Map<String, ClassOrInterfaceDeclaration> classes, SimpleName variable) {
		this.classes = classes;
		this.variable = variable;
	}
	
	public Optional<ResolvedVariable> findDeclaration() {
		if(!(variable.getParentNode().get() instanceof NameExpr || variable.getParentNode().get() instanceof FieldAccessExpr))
			return Optional.empty();
			
		if(variable.getParentNode().isPresent() && variable.getParentNode().get() instanceof FieldAccessExpr) {
			FieldAccessExpr fieldAccess = (FieldAccessExpr)variable.getParentNode().get();
			if(fieldAccess.getScope() instanceof ThisExpr) {
				return seekParents(variable, true);
			} else if(fieldAccess.getName().equals(variable)) {
				return Optional.empty();
			}
		}
		return seekParents(variable, false);
	}

	private Optional<ResolvedVariable> seekParents(Node node, boolean onlyGlobal) {
		Optional<ResolvedVariable> result;
		if(node.getParentNode().isPresent()) {
			Node parent = node.getParentNode().get();
			if(!onlyGlobal) {
				if((result = findInMethodDeclaration(parent)).isPresent()) return result;
				if((result = findLocalVariable(parent)).isPresent()) return result;
			}
			if((result = findInClassDeclaration(parent)).isPresent()) return result;
			return seekParents(parent, onlyGlobal);
		}
		return Optional.empty();
	}

	private Optional<ResolvedVariable> findInClassDeclaration(Node parent) {
		if(parent instanceof ClassOrInterfaceDeclaration && !((ClassOrInterfaceDeclaration)parent).isInterface()) {
			Optional<ResolvedVariable> resolved = findVariableInSuperclass((ClassOrInterfaceDeclaration)parent, false);
			if(resolved.isPresent())
				return resolved;
		}
		return Optional.empty();
	}

	private Optional<ResolvedVariable> findInMethodDeclaration(Node parent) {
		if(parent instanceof MethodDeclaration) {
			Optional<Parameter> declaration = ((MethodDeclaration)parent).getParameters().stream().filter(parameter -> parameter.getName().equals(variable)).findAny();
			if(declaration.isPresent())
				return createResolvedVariable(declaration.get(), VariableType.METHODPARAMETER); 
		}
		return Optional.empty();
	}

	private Optional<ResolvedVariable> findLocalVariable(Node parent) {
		for(Node child : parent.getChildNodes()) {
			if(child instanceof ExpressionStmt)
				child = ((ExpressionStmt)child).getExpression();
			if(child instanceof VariableDeclarationExpr) {
				VariableDeclarationExpr dec = (VariableDeclarationExpr)child;
				for(VariableDeclarator decl : dec.getVariables()) {
					if(decl.getName().equals(variable) && (!decl.getRange().isPresent() || !variable.getRange().isPresent() || (decl.getRange().get().begin.isBefore(variable.getRange().get().begin)|| decl.getRange().get().begin.equals(variable.getRange().get().begin))))
						return createResolvedVariable(decl, VariableType.LOCAL); 
				}
			}
		}
		return Optional.empty();
	}
	
	private Optional<ResolvedVariable> findVariableInSuperclass(ClassOrInterfaceDeclaration classDecl, boolean isSuperclass){
		Optional<VariableDeclarator> declaration = getDeclaratorOfVariableForClass(classDecl);
		if(declaration.isPresent()) 
			return createResolvedVariable(declaration.get(), isSuperclass ? VariableType.SUPERCLASSFIELD : VariableType.CLASSFIELD);
		return classDecl.getExtendedTypes().stream().map(e -> getFullyQualifiedName(classDecl, e)).filter(classes::containsKey).map(classes::get).map(e -> findVariableInSuperclass(e, true)).filter(Optional::isPresent).map(Optional::get).findAny();
	}

	private Optional<VariableDeclarator> getDeclaratorOfVariableForClass(ClassOrInterfaceDeclaration classDecl) {
		return classDecl.getMembers().stream().filter(e -> e instanceof FieldDeclaration).flatMap(e -> ((FieldDeclaration)e).getVariables().stream()).filter(e -> e.getName().equals(variable)).findAny();
	}

	@SuppressWarnings("rawtypes")
	private<T extends Node & NodeWithType & NodeWithSimpleName> Optional<ResolvedVariable> createResolvedVariable(T decl, VariableType type) {
		return Optional.of(new ResolvedVariable(decl.getType(), decl.getName(), decl, type));
	}
}
