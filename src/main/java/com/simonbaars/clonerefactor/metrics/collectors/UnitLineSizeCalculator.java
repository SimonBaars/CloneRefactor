package com.simonbaars.clonerefactor.metrics.collectors;

import java.util.stream.Collectors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.model.FiltersTokens;

public class UnitLineSizeCalculator implements Calculator<MethodDeclaration>, FiltersTokens {
    
	@Override
    public int calculate(MethodDeclaration method) {
    	return getEffectiveTokens(method).map(t -> t.getRange()).filter(r -> r.isPresent()).map(r -> r.get().begin.line).collect(Collectors.toSet()).size();
    }
}