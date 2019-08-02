package com.simonbaars.clonerefactor.metrics.collectors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.ast.interfaces.CalculatesLineSize;

public class UnitLineSizeCalculator implements Calculator<MethodDeclaration>, CalculatesLineSize {
    
	@Override
    public int calculate(MethodDeclaration method) {
    	return lineSize(method);
    }
}