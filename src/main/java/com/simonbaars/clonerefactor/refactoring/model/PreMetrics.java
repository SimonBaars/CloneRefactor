package com.simonbaars.clonerefactor.refactoring.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.metrics.calculators.CalculatesCyclomaticComplexity;
import com.simonbaars.clonerefactor.metrics.calculators.CyclomaticComplexityCalculator;
import com.simonbaars.clonerefactor.metrics.calculators.UnitLineSizeCalculator;
import com.simonbaars.clonerefactor.metrics.calculators.UnitTokenSizeCalculator;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;

public class PreMetrics implements RequiresNodeContext, CalculatesCyclomaticComplexity {
	private final Map<MethodDeclaration, Integer> methodCC = new HashMap<>();
	private final Map<MethodDeclaration, Integer> methodLineSize = new HashMap<>();
	private final Map<MethodDeclaration, Integer> methodTokenSize = new HashMap<>();
	
	private final int tokens;
	private final int nodes;
	private final int lines;
	private final int cc;
	
	public PreMetrics(Sequence s) {
		s.getLocations().stream().map(m -> getMethod(m.getFirstNode())).filter(e -> e.isPresent()).map(o -> o.get()).collect(Collectors.toSet()).forEach(m -> {
			methodCC.put(m, new CyclomaticComplexityCalculator().calculate(m));
			methodLineSize.put(m, new UnitLineSizeCalculator().calculate(m));
			methodTokenSize.put(m, new UnitTokenSizeCalculator().calculate(m));
		});
		
		this.tokens = s.getTotalTokenVolume();
		this.nodes = s.getTotalNodeVolume();
		this.lines = s.getTotalEffectiveLineVolume();
		this.cc = calculateCCIncrease(s.getLocations().stream().flatMap(l -> l.getContents().getTopLevelNodes().stream()));
	}

	public int getTokens() {
		return tokens;
	}

	public int getNodes() {
		return nodes;
	}

	public int getLines() {
		return lines;
	}

	public int getCc() {
		return cc;
	}
}
