package com.simonbaars.clonerefactor.metrics.context.interfaces;

import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public interface RequiresNodeContext {
	public default Optional<MethodDeclaration> getMethod(Node n1) {
		return getNode(MethodDeclaration.class, n1);
	}
	
	public default Optional<ConstructorDeclaration> getConstructor(Node n1) {
		return getNode(ConstructorDeclaration.class, n1);
	}
	
	public default ClassOrInterfaceDeclaration getClass(Node n1) {
		return getNode(ClassOrInterfaceDeclaration.class, n1).get();
	}
	
	public default CompilationUnit getCompilationUnit(Node n1) {
		return getNode(CompilationUnit.class, n1).get();
	}
	
	public default EnumDeclaration getEnum(Node n1) {
		return getNode(EnumDeclaration.class, n1).get();
	}
	
	@SuppressWarnings("unchecked")
	public default<T extends Node> Optional<T> getNode(Class<T> type, Node n1) {
		while (!n1.getClass().isAssignableFrom(type)) {
			if(n1.getParentNode().isPresent()) {
				n1 = n1.getParentNode().get();
			} else return Optional.empty();
		}
		return Optional.of((T)n1);
	}
}
