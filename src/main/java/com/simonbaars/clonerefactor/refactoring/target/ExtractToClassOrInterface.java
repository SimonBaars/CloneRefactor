package com.simonbaars.clonerefactor.refactoring.target;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;

public class ExtractToClassOrInterface implements ExtractionTarget, RequiresNodeContext {
	private final ClassOrInterfaceDeclaration classOrInterface;

	public ExtractToClassOrInterface(ClassOrInterfaceDeclaration classOrInterface) {
		super();
		this.classOrInterface = classOrInterface;
	}
	
	public ExtractToClassOrInterface(Sequence sequence) {
		super();
		this.classOrInterface = getClass(sequence.getAny().getFirstNode()).get();
	}

	public ClassOrInterfaceDeclaration getClassOrInterface() {
		return classOrInterface;
	}

	@Override
	public void extract(MethodDeclaration d) {
		classOrInterface.getMembers().add(d);
	}

	@Override
	public void save() {
		
	}
}
