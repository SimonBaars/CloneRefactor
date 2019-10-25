package com.simonbaars.clonerefactor.metrics;

import java.util.List;

import com.simonbaars.clonerefactor.context.Metric;
import com.simonbaars.clonerefactor.context.StatType;
import com.simonbaars.clonerefactor.context.analyze.CloneContents;
import com.simonbaars.clonerefactor.context.analyze.CloneLocation;
import com.simonbaars.clonerefactor.context.analyze.CloneRefactorability;
import com.simonbaars.clonerefactor.context.analyze.CloneRelation;
import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.metrics.ProblemType;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.settings.progress.Progress;

public class MetricCollector implements CalculatesPercentages {
	private Metrics metrics = new Metrics();
	private final CloneRelation relationFinder;
	private final CloneLocation locationFinder = new CloneLocation();
	private final CloneContents contentsFinder = new CloneContents();
	private final CloneRefactorability extractFinder = new CloneRefactorability();
	
	public MetricCollector() {
		this.relationFinder = new CloneRelation();
	}

	public void reportFoundNode(Location l) {
		metrics.incrementGeneralStatistic(Metric.NODES, StatType.TOTAL, 1);
		metrics.incrementGeneralStatistic(Metric.TOKENS, StatType.TOTAL, l.getNumberOfTokens());
		metrics.incrementGeneralStatistic(Metric.LINES, StatType.TOTAL, l.getNumberOfLines());
	}

	public Metrics reportClones(List<Sequence> clones, Progress progress) {
		if(clones.isEmpty())
			metrics.generalStats.increment("Projects without clone classes");
		metrics.generalStats.increment("Clone classes", clones.size());
		metrics.averages.addTo("Amount of clone classes", clones.size());
		for(Sequence clone : clones) {
			reportClone(clone);
			progress.next();
		}
		saveDuplicationPercentage(metrics.generalStats.get("Total Nodes"), metrics.generalStats.get("Cloned Nodes"));
		return metrics;
	}

	private void saveDuplicationPercentage(int total, int cloned) {
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
		for(Location l : clone.getLocations())
			reportClonedLocation(l);
		
		clone.setMetrics(relationFinder, extractFinder);
		metrics.amountPerRelation.increment(clone.getRelationType());
		metrics.amountPerExtract.increment(clone.getRefactorability());
		metrics.averages.addTo("Statements per clone class", clone.getNodeSize());
	}
	
	public void reassessRelation(Sequence clone) {
		clone.setRelation(relationFinder);
	}

	private void reportClonedLocation(Location l) {
		metrics.averages.addTo("Cloned nodes", l.getNumberOfNodes());
		metrics.averages.addTo("Cloned tokens", l.getNumberOfTokens());
		metrics.averages.addTo("Cloned lines", l.getNumberOfLines());
		metrics.incrementGeneralStatistic(Metric.TOKENS, StatType.CLONED, l.getNumberOfTokens());
		metrics.incrementGeneralStatistic(Metric.NODES, StatType.CLONED, l.getNumberOfNodes());
		metrics.incrementGeneralStatistic(Metric.LINES, StatType.CLONED, l.getNumberOfLines());
		l.setMetrics(locationFinder);
		l.getContents().setMetrics(contentsFinder);
		metrics.amountPerLocation.increment(l.getLocationType());
		metrics.amountPerContents.increment(l.getContents().getContentsType());
	}

	public Metrics getMetrics() {
		return metrics;
	}

	public void resetMetrics() {
		metrics = new Metrics();
	}
}
