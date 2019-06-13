package com.simonbaars.clonerefactor.ast.compare;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

public class MethodDeclarationProxy {
	private final ResolvedMethodDeclaration declaration;
	private final String fullyQualifiedSignature;
	private final String fullyQualifiedArguments;
	
	public MethodDeclarationProxy(ResolvedMethodDeclaration declaration) {
		super();
		this.declaration = declaration;
		this.fullyQualifiedSignature = declaration.getQualifiedSignature();
		this.fullyQualifiedArguments = getOnlyArguments(fullyQualifiedSignature);
	}
	
	private String getOnlyArguments(String methodSignature) {
		return methodSignature.substring(methodSignature.indexOf('('));
	}

	public ResolvedMethodDeclaration getDeclaration() {
		return declaration;
	}

	public String getFullyQualifiedSignature() {
		return fullyQualifiedSignature;
	}

	public String getFullyQualifiedArguments() {
		return fullyQualifiedArguments;
	}
	
	
}
