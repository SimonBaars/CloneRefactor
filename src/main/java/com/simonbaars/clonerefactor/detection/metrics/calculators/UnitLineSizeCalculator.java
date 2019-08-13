package com.simonbaars.clonerefactor.detection.metrics.calculators;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.detection.metrics.Calculator;

import clonegraph.interfaces.CalculatesLineSize;

public class UnitLineSizeCalculator implements Calculator<MethodDeclaration>, CalculatesLineSize {
    
	@Override
    public int calculate(MethodDeclaration method) {
    	return lineSize(method);
    }
}