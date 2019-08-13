package com.simonbaars.clonerefactor.detection.metrics.calculators;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.clonegraph.interfaces.CalculatesLineSize;
import com.simonbaars.clonerefactor.detection.metrics.interfaces.Calculator;

public class UnitLineSizeCalculator implements Calculator<MethodDeclaration>, CalculatesLineSize {
    
	@Override
    public int calculate(MethodDeclaration method) {
    	return lineSize(method);
    }
}