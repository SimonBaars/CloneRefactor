package com.simonbaars.clonerefactor.refactoring.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.utils.Pair;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.metrics.context.enums.Risk;

public class RiskProfile {
	private final Map<MethodDeclaration, Pair<Integer, Integer>> changedProblemSize = new HashMap<MethodDeclaration, Pair<Integer, Integer>>();
	private final ProblemType type;
	private final int newMethodProblemSize;
	
	public RiskProfile(ProblemType type, int newMethodProblemSize) {
		super();
		this.type = type;
		this.newMethodProblemSize = newMethodProblemSize;
	}
	
	public ProblemType getType() {
		return type;
	}

	public int getNewMethodProblemSize() {
		return newMethodProblemSize;
	}

	public RiskProfile calculateRisk(Map<MethodDeclaration, Integer> post, Map<MethodDeclaration, Integer> pre) {
		pre.entrySet().forEach(e -> {
			getMetricChanges(post, e.getKey()).ifPresent(nPre -> {
				changedProblemSize.put(e.getKey(), new Pair<Integer, Integer>(nPre, e.getValue()));
			});
		});
		return this;
	}
	
	public Optional<Integer> getMetricChanges(Map<MethodDeclaration, Integer> map, MethodDeclaration key){
		if(map.containsKey(key))
			return Optional.of(map.get(key));
		return Optional.empty();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(type+System.lineSeparator());
		builder.append("Created a new method with a "+type.getRisk(newMethodProblemSize).lowercase()+" risk "+type+" of "+newMethodProblemSize+"." + System.lineSeparator());
		builder.append("Removing duplicate blocks changed "+changedProblemSize.size()+" methods." +System.lineSeparator());
		for(Entry<MethodDeclaration, Pair<Integer, Integer>> e : changedProblemSize.entrySet()) {
			builder.append("The method \""+e.getKey().getSignature().asString()+"\" went from "+e.getValue().a+" to "+e.getValue().b+" "+type+". ");
			builder.append(riskChange(type.getRisk(e.getValue().a), type.getRisk(e.getValue().b))+System.lineSeparator());
		}
		builder.append(System.lineSeparator());
		return builder.toString();
	}

	public static String riskChange(Risk risk, Risk risk2) {
		if(risk == risk2)
			return "This did not influence the risk category of this method, it is still "+risk.lowercase()+" risk.";
		else if(risk.ordinal() > risk2.ordinal())
			return "This increased the risk category of this method from "+risk.lowercase()+" to "+risk2.lowercase();
		return "This decreased the risk category of this method from "+risk.lowercase()+" to "+risk2.lowercase();
	}
}
