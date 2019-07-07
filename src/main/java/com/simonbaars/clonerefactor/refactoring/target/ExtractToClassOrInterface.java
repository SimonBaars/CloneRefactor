package com.simonbaars.clonerefactor.refactoring.target;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.metrics.enums.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;

public class ExtractToClassOrInterface implements ExtractionTarget, RequiresNodeContext {
	private final ClassOrInterfaceDeclaration classOrInterface;

	protected ExtractToClassOrInterface(ClassOrInterfaceDeclaration classOrInterface) {
		super();
		this.classOrInterface = classOrInterface;
	}
	
	public ExtractToClassOrInterface(Sequence sequence) {
		super();
		this.classOrInterface = getClass(sequence.getAny().getContents().getNodes().get(0));
	}

	public ClassOrInterfaceDeclaration getClassOrInterface() {
		return classOrInterface;
	}

	@Override
	public void extract(MethodDeclaration d) {
		classOrInterface.getMembers().add(d);
	}
}
