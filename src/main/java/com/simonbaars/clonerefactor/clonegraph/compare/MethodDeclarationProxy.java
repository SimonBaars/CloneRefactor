package com.simonbaars.clonerefactor.clonegraph.compare;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;

public class MethodDeclarationProxy {
	private final ResolvedMethodDeclaration declaration;
	private final String fullyQualifiedSignature;
	private final String fullyQualifiedArguments;
	private ResolvedType returnType;
	
	public MethodDeclarationProxy(ResolvedMethodDeclaration declaration) {
		super();
		this.declaration = declaration;
		this.fullyQualifiedSignature = declaration.getQualifiedSignature();
		this.fullyQualifiedArguments = getOnlyArguments(fullyQualifiedSignature);
		this.returnType = declaration.getReturnType();
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

	public ResolvedType getReturnType() {
		return returnType;
	}

	public void setReturnType(ResolvedType returnType) {
		this.returnType = returnType;
	}
	
	public boolean equalsType1(MethodDeclarationProxy m) {
		return fullyQualifiedSignature.equals(m.fullyQualifiedSignature);
	}
	
	public boolean equalsType2(MethodDeclarationProxy m) {
		return returnType.equals(m.returnType) && fullyQualifiedArguments.equals(m.fullyQualifiedArguments);
	}
	
	public int hashcodeType1() {
		return fullyQualifiedSignature.hashCode();
	}
	
	public int hashcodeType2() {
		return 31 * returnType.hashCode() + fullyQualifiedArguments.hashCode();
	}
}
