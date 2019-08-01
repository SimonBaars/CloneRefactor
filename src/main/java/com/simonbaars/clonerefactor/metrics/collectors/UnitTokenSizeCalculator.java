package com.simonbaars.clonerefactor.metrics.collectors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.model.FiltersTokens;

public class UnitTokenSizeCalculator implements Calculator<MethodDeclaration>, FiltersTokens {
    @Override
    public int calculate(MethodDeclaration method) {
    	return Math.toIntExact(getEffectiveTokens(method.getTokenRange()).count());
    }
}