package com.simonbaars.clonerefactor.refactoring.target;

import com.github.javaparser.ast.body.MethodDeclaration;

public interface ExtractionTarget {
	public void extract(MethodDeclaration d);
}
