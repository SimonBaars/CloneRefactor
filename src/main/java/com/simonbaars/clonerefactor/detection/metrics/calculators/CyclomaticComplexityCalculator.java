package com.simonbaars.clonerefactor.detection.metrics.calculators;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.detection.metrics.interfaces.CalculatesCyclomaticComplexity;
import com.simonbaars.clonerefactor.detection.metrics.interfaces.Calculator;

public class CyclomaticComplexityCalculator implements Calculator<MethodDeclaration>, CalculatesCyclomaticComplexity {
    @Override
    public int calculate(MethodDeclaration method) {
        return calculateCC(method);
    }
}