package com.simonbaars.clonerefactor.refactoring.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

	public RiskProfile calculateRisk(Map<MethodDeclaration, Integer> methodCC2, Map<MethodDeclaration, Integer> methodCC3) {
		methodCC2.entrySet().forEach(e -> {
			if(methodCC3.containsKey(e.getKey()))
				changedProblemSize.put(e.getKey(), new Pair<Integer, Integer>(methodCC3.get(e.getKey()), e.getValue()));
		});
		return this;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(type+System.lineSeparator());
		builder.append("Created a new method with a "+type.getRisk(newMethodProblemSize)+" risk "+type+" of "+newMethodProblemSize+"." + System.lineSeparator());
		builder.append("Removing duplicate blocks changed "+changedProblemSize.size()+" methods." +System.lineSeparator());
		for(Entry<MethodDeclaration, Pair<Integer, Integer>> e : changedProblemSize.entrySet()) {
			builder.append("The method named \""+e.getKey().getNameAsString()+"\" went from "+e.getValue().a+" "+type+" to "+e.getValue().b+" "+type+". ");
			builder.append(riskChange(type.getRisk(e.getValue().a), type.getRisk(e.getValue().b)));
		}
		builder.append(System.lineSeparator()+System.lineSeparator());
		return builder.toString();
	}

	private String riskChange(Risk risk, Risk risk2) {
		if(risk == risk2)
			return "This did not influence the risk category of this method, it is still "+risk.lowercase()+" risk.";
		else if(risk.ordinal() > risk2.ordinal())
			return "This increased the risk category of this method from "+risk.lowercase()+" to "+risk2.lowercase();
		return "This decreased the risk category of this method from "+risk.lowercase()+" to "+risk2.lowercase();
	}
}
