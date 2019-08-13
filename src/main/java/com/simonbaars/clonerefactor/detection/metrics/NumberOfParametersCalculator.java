package com.simonbaars.clonerefactor.detection.metrics;

import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * Simply counts the number of parameters on a method
 */
public class NumberOfParametersCalculator implements Calculator<MethodDeclaration> {
    @Override
    public int calculate(MethodDeclaration method) {
        return method.getParameters().size();
    }
}