package com.simonbaars.clonerefactor.refactoring.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.simonbaars.clonerefactor.ast.interfaces.CalculatesLineSize;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.metrics.calculators.CalculatesCyclomaticComplexity;
import com.simonbaars.clonerefactor.metrics.calculators.CyclomaticComplexityCalculator;
import com.simonbaars.clonerefactor.metrics.calculators.UnitLineSizeCalculator;
import com.simonbaars.clonerefactor.metrics.calculators.UnitTokenSizeCalculator;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;

public class PostMetrics implements RequiresNodeContext, CalculatesLineSize, CalculatesCyclomaticComplexity {
	private final Map<MethodDeclaration, Integer> methodCC = new HashMap<>();
	private final Map<MethodDeclaration, Integer> methodLineSize = new HashMap<>();
	private final Map<MethodDeclaration, Integer> methodTokenSize = new HashMap<>();
	
	private final int addedTokenVolume;
	private final int addedLineVolume;
	private final int addedNodeVolume;
	
	private final int unitInterfaceSize;
	private final int cc;
	
	private final int newMethodTokens;
	private final int newMethodLines;
	
	
	public PostMetrics(MethodDeclaration newMethod, Optional<ClassOrInterfaceDeclaration> classOrInterface, List<Statement> methodcalls) {
		methodcalls.stream().map(m -> getMethod(m)).filter(e -> e.isPresent()).map(o -> new JavaParser().parseBodyDeclaration(o.get().toString())).filter(o -> o.isSuccessful() && o.getResult().get() instanceof MethodDeclaration).map(o -> (MethodDeclaration)o.getResult().get()).collect(Collectors.toSet()).forEach(m -> {
			methodCC.put(m, new CyclomaticComplexityCalculator().calculate(m));
			methodLineSize.put(m, new UnitLineSizeCalculator().calculate(m));
			methodTokenSize.put(m, new UnitTokenSizeCalculator().calculate(m));
		});
		
		newMethod = (MethodDeclaration)new JavaParser().parseBodyDeclaration(newMethod.toString()).getResult().get();
		methodcalls = methodcalls.stream().map(e -> new JavaParser().parseStatement(e.toString())).filter(e -> e.isSuccessful()).map(e -> e.getResult().get()).collect(Collectors.toList());
		Optional<TypeDeclaration<?>> c = classOrInterface.isPresent() ? Optional.of(new JavaParser().parseTypeDeclaration(classOrInterface.get().toString()).getResult().get()) : Optional.empty();
		
		addedTokenVolume = calculateAddedVolume(this::countTokens, c, newMethod, methodcalls);
		addedLineVolume = methodcalls.size() + (classOrInterface.isPresent() ? 2 : 0) + lineSize(newMethod);
		addedNodeVolume = addedLineVolume - 2;
		unitInterfaceSize = newMethod.getParameters().size();
		cc = calculateCC(newMethod);
		
		this.newMethodTokens = countTokens(newMethod);
		this.newMethodLines = lineSize(newMethod);
	}
	
	public int amountOfNodes(Node n) {
		return 1;
	}
	
	private int calculateAddedVolume(Function<Node, Integer> calculateMetric, Optional<? extends Node> classOrInterface,
			MethodDeclaration newMethod, List<Statement> methodcalls) {
		int total = 0;
		if(classOrInterface.isPresent()) {
			total += calculateMetric.apply(classOrInterface.get());
		} else {
			total += calculateMetric.apply(newMethod);
		}
		total += methodcalls.stream().mapToInt(calculateMetric::apply).sum();
		return total;
	}

	public int getAddedTokenVolume() {
		return addedTokenVolume;
	}

	public int getAddedLineVolume() {
		return addedLineVolume;
	}

	public int getAddedNodeVolume() {
		return addedNodeVolume;
	}

	public int getUnitInterfaceSize() {
		return unitInterfaceSize;
	}

	public int getCc() {
		return cc;
	}
	
	public CombinedMetrics combine(PreMetrics metrics) {
		return new CombinedMetrics(getCc()-metrics.getCc(), getAddedLineVolume()-metrics.getLines(), getAddedTokenVolume()-metrics.getTokens(), getAddedNodeVolume()-metrics.getNodes(), getUnitInterfaceSize(), metrics.getNodes(), metrics.getTokens(), metrics.getLines(),
				new RiskProfile(ProblemType.UNITCOMPLEXITY, cc).calculateRisk(methodCC, metrics.getMethodCC()), new RiskProfile(ProblemType.LINEVOLUME, newMethodLines).calculateRisk(methodLineSize, metrics.getMethodLineSize()), new RiskProfile(ProblemType.TOKENVOLUME, newMethodTokens).calculateRisk(methodTokenSize, metrics.getMethodTokenSize()));
	}
}
