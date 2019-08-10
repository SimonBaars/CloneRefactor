package com.simonbaars.clonerefactor.refactoring.model;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.ast.MetricObserver;
import com.simonbaars.clonerefactor.datatype.map.CountMap;
import com.simonbaars.clonerefactor.datatype.map.SimpleTable;
import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.metrics.MetricCollector;
import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.refactoring.enums.MethodType;

public class CombinedMetrics implements CalculatesPercentages {
	private final int ccIncrease;
	private final int lineSizeIncrease;
	private final int tokenSizeIncrease;
	private final int nodeSizeIncrease;
	private final int unitInterfaceSizeIncrease;
	
	private final int duplicateNodesIncrease;
	private final int duplicateTokensIncrease;
	private final int duplicateLinesIncrease;
	
	private final RiskProfile complexity;
	private final RiskProfile lineVolume;
	private final RiskProfile tokenVolume;
	
	public CombinedMetrics(int ccIncrease, int lineSizeIncrease, int tokenSizeIncrease, int nodeSizeIncrease,
			int unitInterfaceSizeIncrease, int nodes, int tokens, int lines, RiskProfile complexity, RiskProfile lineVolume, RiskProfile tokenVolume) {
		super();
		this.ccIncrease = ccIncrease;
		this.lineSizeIncrease = lineSizeIncrease;
		this.tokenSizeIncrease = tokenSizeIncrease;
		this.nodeSizeIncrease = nodeSizeIncrease;
		this.unitInterfaceSizeIncrease = unitInterfaceSizeIncrease;
		
		this.duplicateNodesIncrease = -nodes;
		this.duplicateTokensIncrease = -tokens;
		this.duplicateLinesIncrease = -lines;
		
		this.complexity = complexity;
		this.lineVolume = lineVolume;
		this.tokenVolume = tokenVolume;
	}

	public int getCcIncrease() {
		return ccIncrease;
	}

	public int getLineSizeIncrease() {
		return lineSizeIncrease;
	}

	public int getTokenSizeIncrease() {
		return tokenSizeIncrease;
	}

	public int getNodeSizeIncrease() {
		return nodeSizeIncrease;
	}

	public int getUnitInterfaceSizeIncrease() {
		return unitInterfaceSizeIncrease;
	}
	
	public String save(MetricCollector collector, CountMap<String> metrics) {
		collector.getMetrics().incrementGeneralStatistic("CC Increase", ccIncrease);
		collector.getMetrics().incrementGeneralStatistic("Lines Increase", lineSizeIncrease);
		collector.getMetrics().incrementGeneralStatistic("Tokens Increase", tokenSizeIncrease);
		collector.getMetrics().incrementGeneralStatistic("Nodes Increase", nodeSizeIncrease);
		collector.getMetrics().incrementGeneralStatistic("Unit Size Increase", unitInterfaceSizeIncrease);
		collector.getMetrics().incrementGeneralStatistic("Duplicate Nodes Increase", duplicateNodesIncrease);
		collector.getMetrics().incrementGeneralStatistic("Duplicate Token Increase", duplicateTokensIncrease);
		collector.getMetrics().incrementGeneralStatistic("Duplicate Lines Increase", duplicateLinesIncrease);
		
		metrics.increment(MetricObserver.metricTotalSize(ProblemType.UNITCOMPLEXITY), ccIncrease);
		metrics.increment(MetricObserver.metricTotalSize(ProblemType.TOKENVOLUME), tokenSizeIncrease);
		metrics.increment(MetricObserver.metricTotalSize(ProblemType.LINEVOLUME), lineSizeIncrease);
		metrics.increment(MetricObserver.metricTotalSize(ProblemType.UNITINTERFACESIZE), unitInterfaceSizeIncrease);
		metrics.increment("Total Nodes", nodeSizeIncrease);
		metrics.increment("Cloned Nodes", nodeSizeIncrease);
		metrics.increment("Cloned Tokens", nodeSizeIncrease);
		metrics.increment("Cloned Lines", nodeSizeIncrease);
		return createString(collector, metrics);
	}

	public String createString(MetricCollector collector, CountMap<String> metrics) {
		return "== System Quality Metrics =="+System.lineSeparator()+
				tellWhatHappened("Total Cyclomatic Complexity", metrics.get(MetricObserver.metricTotalSize(ProblemType.UNITCOMPLEXITY)), ccIncrease) +
				tellWhatHappened("Total Unit Interface Size",metrics.get(MetricObserver.metricTotalSize(ProblemType.UNITINTERFACESIZE)), unitInterfaceSizeIncrease) +
				tellWhatHappened("Total Unit Line Size",metrics.get(MetricObserver.metricTotalSize(ProblemType.LINEVOLUME)), lineSizeIncrease) +
				tellWhatHappened("Total Unit Token Size",metrics.get(MetricObserver.metricTotalSize(ProblemType.TOKENVOLUME)), tokenSizeIncrease) +
				tellWhatHappened("Total Nodes",metrics.get("Total Nodes"), nodeSizeIncrease) + System.lineSeparator() +
				tellWhatHappened("Duplicated Nodes",metrics.get("Cloned Nodes"), duplicateNodesIncrease) +
				tellWhatHappened("Duplicated Tokens",metrics.get("Cloned Tokens"), duplicateTokensIncrease) +
				tellWhatHappened("Duplicated Lines",metrics.get("Cloned Lines"), duplicateLinesIncrease) + System.lineSeparator() +
				"== Risk Profiles =="+ System.lineSeparator() + 
				complexity + lineVolume + tokenVolume + 
				"The new method has a "+ProblemType.UNITINTERFACESIZE.getRisk(unitInterfaceSizeIncrease).lowercase()+" risk "+ProblemType.UNITINTERFACESIZE+" of "+unitInterfaceSizeIncrease+"."+System.lineSeparator() + System.lineSeparator() +
				getDuplicationRiskProfile(metrics.get("Total Nodes")-nodeSizeIncrease, metrics.get("Cloned Nodes")-duplicateNodesIncrease, metrics.get("Total Nodes"), metrics.get("Cloned Nodes"));
	}
	
	private String getDuplicationRiskProfile(int totalBefore, int clonedBefore, int totalAfter, int clonedAfter) {
		double percBefore = calcPercentage(clonedBefore, totalBefore);
		double percAfter = calcPercentage(clonedAfter, totalAfter);
		return "Duplication went from "+String.format("%.2f", percBefore)+"% to "+String.format("%.2f", percAfter)+"%. "+RiskProfile.riskChange(ProblemType.DUPLICATION.getRisk(Math.toIntExact(Math.round(percBefore))), ProblemType.DUPLICATION.getRisk(Math.toIntExact(Math.round(percAfter)))).replace("this method", "the duplication in this codebase");
	}

	private String tellWhatHappened(String metric, int total, int increase) {
		StringBuilder stringBuilder = new StringBuilder(metric+" ");
		if(increase == 0) {
			stringBuilder.append("did not change and is still "+total);
		} else {
			if(increase>0) {
				stringBuilder.append("increased");
			} else {
				stringBuilder.append("decreased");
			}
			stringBuilder.append(" by "+Math.abs(increase)+" from "+(total-increase)+" to "+total);
		}
		stringBuilder.append("."+System.lineSeparator());
		return stringBuilder.toString();
	}

	public void saveTable(SimpleTable res, Sequence s, String systemName, MethodDeclaration decl, MethodType type) {
		res.addRow(systemName, s.getNodeSize(), s.getTokenSize(), s.getRelation().getType(), type, decl.getParameters().size(), duplicateTokensIncrease, ccIncrease, unitInterfaceSizeIncrease, tokenSizeIncrease, duplicateNodesIncrease, nodeSizeIncrease);
	}
}
