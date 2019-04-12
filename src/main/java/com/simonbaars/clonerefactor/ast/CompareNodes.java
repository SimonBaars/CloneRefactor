package com.simonbaars.clonerefactor.ast;

import java.util.stream.IntStream;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.simonbaars.clonerefactor.model.CompareRules;

public class CompareNodes {
	private CompareRules rules;
	
	public CompareNodes(CompareRules rules) {
		super();
		this.rules = rules;
	}
	
	public CompareNodes() {
		super();
		this.rules = new CompareRules();
	}

	public boolean compare(Node n1, Node n2) {
		if(n1.getClass() != n2.getClass())
			return false;
		else if(n1 instanceof MethodDeclaration)
			return compareMethodDeclarations((MethodDeclaration)n1, (MethodDeclaration)n2);
		else if(n1 instanceof ClassOrInterfaceDeclaration)
			return compareClassDeclarations((ClassOrInterfaceDeclaration)n1, (ClassOrInterfaceDeclaration)n2);
		else if(n1 instanceof ForEachStmt)
			return compareForEachStmt((ForEachStmt)n1, (ForEachStmt)n2);
		else if(n1 instanceof DoStmt)
			return compareDoStmt((DoStmt)n1, (DoStmt)n2);
		else if(n1 instanceof ForStmt)
			return compareForStmt((ForStmt)n1, (ForStmt)n2);
		else if(n1 instanceof WhileStmt)
			return compareWhileStmt((WhileStmt)n1, (WhileStmt)n2);
		return n1.equals(n2);
	}

	private boolean compareForEachStmt(ForEachStmt n1, ForEachStmt n2) {
		return n1.getVariable().equals(n2.getVariable()) &&
				n1.getIterable().equals(n2.getIterable());
	}

	private boolean compareDoStmt(DoStmt n1, DoStmt n2) {
		return n1.getCondition().equals(n2.getCondition());
	}

	private boolean compareForStmt(ForStmt n1, ForStmt n2) {
		return n1.getCompare().equals(n2.getCompare()) && 
				n1.getInitialization().equals(n2.getInitialization()) &&
				n1.getUpdate().equals(n2.getUpdate());
	}

	private boolean compareWhileStmt(WhileStmt n1, WhileStmt n2) {
		return n1.getCondition().equals(n2.getCondition());
	}

	private boolean compareClassDeclarations(ClassOrInterfaceDeclaration n1, ClassOrInterfaceDeclaration n2) {
		if(rules.isCheckClassNames() && n1.getNameAsString().equals(n2.getNameAsString()))
			return false;
		return n1.isInterface() == n2.isInterface() &&
				n1.getModifiers().equals(n2.getModifiers()) && 
				n1.getImplementedTypes().equals(n2.getImplementedTypes()) &&
				n1.getExtendedTypes().equals(n2.getExtendedTypes());
	}

	private boolean compareMethodDeclarations(MethodDeclaration n1, MethodDeclaration n2) {
		if(rules.isCheckMethodNames() && !n1.getNameAsString().equals(n2.getNameAsString()))
			return false;
		return n1.getParameters().size() == n2.getParameters().size() &&
				IntStream.range(0, n1.getParameters().size()).boxed().allMatch(i -> compareParameters(n1.getParameters().get(i), n2.getParameters().get(i))) &&
				n1.getType().equals(n2.getType()) &&
				n1.getModifiers().equals(n2.getModifiers());
	}

	private boolean compareParameters(Parameter p1, Parameter p2) {
		if(rules.isCheckParameterNames() && !p1.getNameAsString().equals(p2.getNameAsString()))
			return false;
		return p1.getType().equals(p2.getType());
	}
}
