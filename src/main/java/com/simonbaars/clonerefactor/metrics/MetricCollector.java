package com.simonbaars.clonerefactor.metrics;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.metrics.context.Metric;
import com.simonbaars.clonerefactor.metrics.context.StatType;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneContents;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneLocation;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneRefactorability;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class MetricCollector implements CalculatesPercentages {
	private final Metrics metrics = new Metrics();
	private final CloneRelation relationFinder;
	private final CloneLocation locationFinder = new CloneLocation();
	private final CloneContents contentsFinder = new CloneContents();
	private final CloneRefactorability extractFinder = new CloneRefactorability();
	
	public MetricCollector(Map<String, ClassOrInterfaceDeclaration> classes) {
		this.relationFinder = new CloneRelation(classes);
	}

	public void reportFoundNode(Location l) {
		metrics.incrementGeneralStatistic(Metric.NODES, StatType.TOTAL, 1);
		metrics.incrementGeneralStatistic(Metric.TOKENS, StatType.TOTAL, l.getAmountOfTokens());
		metrics.incrementGeneralStatistic(Metric.LINES, StatType.TOTAL, l.getAmountOfLines());
	}

	public Metrics reportClones(List<Sequence> clones) {
		if(clones.isEmpty())
			metrics.generalStats.increment("Projects without clone classes");
		metrics.generalStats.increment("Clone classes", clones.size());
		metrics.averages.addTo("Amount of clone classes", clones.size());
		for(Sequence clone : clones)
			reportClone(clone);
		saveDuplicationPercentage(metrics, metrics.generalStats.get("Total Nodes"), metrics.generalStats.get("Cloned Nodes"));
		return metrics;
	}

	private void saveDuplicationPercentage(Metrics metrics2, int total, int cloned) {
		double percBefore = calcPercentage(cloned, total);
		metrics.averages.addTo("Percentage Duplicated", percBefore);
		metrics.incrementGeneralStatistic(ProblemType.DUPLICATION, round(percBefore));
	}

	private void reportClone(Sequence clone) {
		metrics.averages.addTo("Clone class size", clone.size());
		metrics.amountPerCloneClassSize.increment(clone.size());
		metrics.amountPerNodes.increment(clone.getNodeSize());
		metrics.amountPerTotalNodeVolume.increment(clone.getTotalNodeVolume());
		metrics.amountPerEffectiveLines.increment(clone.getEffectiveLineSize());
		metrics.amountPerTotalEffectiveLineVolume.increment(clone.getTotalLineVolume());
		clone.setMetrics(relationFinder, extractFinder);
		metrics.amountPerRelation.increment(clone.getRelationType());
		metrics.amountPerExtract.increment(clone.getRefactorability());
		metrics.averages.addTo("Statements per clone class", clone.getNodeSize());
		for(Location l : clone.getLocations())
			reportClonedLocation(l);
	}
	
	public void reassessRelation(Sequence clone) {
		clone.setRelation(relationFinder);
	}

	private void reportClonedLocation(Location l) {
		metrics.averages.addTo("Cloned nodes", l.getAmountOfNodes());
		metrics.averages.addTo("Cloned tokens", l.getAmountOfTokens());
		metrics.averages.addTo("Cloned lines", l.getAmountOfLines());
		metrics.incrementGeneralStatistic(Metric.TOKENS, StatType.CLONED, l.getAmountOfTokens());
		metrics.incrementGeneralStatistic(Metric.NODES, StatType.CLONED, l.getAmountOfNodes());
		metrics.incrementGeneralStatistic(Metric.LINES, StatType.CLONED, l.getAmountOfLines());
		l.setMetrics(locationFinder);
		l.getContents().setMetrics(contentsFinder);
		metrics.amountPerLocation.increment(l.getLocationType());
		metrics.amountPerContents.increment(l.getContents().getContentsType());
	}

	public Metrics getMetrics() {
		return metrics;
	}
}
