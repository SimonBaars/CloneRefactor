package com.simonbaars.clonerefactor.detection.metrics;

import com.github.javaparser.ast.body.MethodDeclaration;

public class CyclomaticComplexityCalculator implements Calculator<MethodDeclaration>, CalculatesCyclomaticComplexity {
    @Override
    public int calculate(MethodDeclaration method) {
        return calculateCC(method);
    }
}