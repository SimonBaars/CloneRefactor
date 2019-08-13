package com.simonbaars.clonerefactor.detection.metrics.calculators;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.detection.metrics.CalculatesCyclomaticComplexity;
import com.simonbaars.clonerefactor.detection.metrics.Calculator;

public class CyclomaticComplexityCalculator implements Calculator<MethodDeclaration>, CalculatesCyclomaticComplexity {
    @Override
    public int calculate(MethodDeclaration method) {
        return calculateCC(method);
    }
}