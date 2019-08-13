package com.simonbaars.clonerefactor.detection.metrics.calculators;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.detection.metrics.interfaces.Calculator;

/**
 * Simply counts the number of parameters on a method
 */
public class NumberOfParametersCalculator implements Calculator<MethodDeclaration> {
    @Override
    public int calculate(MethodDeclaration method) {
        return method.getParameters().size();
    }
}