package com.simonbaars.clonerefactor.metrics.calculators;

import com.github.javaparser.ast.body.MethodDeclaration;

public class CyclomaticComplexityCalculator implements Calculator<MethodDeclaration>, CalculatesCyclomaticComplexity {
    @Override
    public int calculate(MethodDeclaration method) {
        return calculateCyclomaticComplexity(method);
    }
}