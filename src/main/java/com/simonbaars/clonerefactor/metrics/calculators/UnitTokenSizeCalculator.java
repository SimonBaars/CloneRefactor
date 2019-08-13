package com.simonbaars.clonerefactor.metrics.calculators;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.detection.model.FiltersTokens;

public class UnitTokenSizeCalculator implements Calculator<MethodDeclaration>, FiltersTokens {
    @Override
    public int calculate(MethodDeclaration method) {
    	return countTokens(method);
    }
}