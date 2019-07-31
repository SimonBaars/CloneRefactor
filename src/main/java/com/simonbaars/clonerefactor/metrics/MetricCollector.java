package com.simonbaars.clonerefactor.metrics;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.datatype.map.SetMap;
import com.simonbaars.clonerefactor.metrics.context.Metric;
import com.simonbaars.clonerefactor.metrics.context.StatType;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneContents;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneLocation;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneRefactorability;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class MetricCollector {
	private final SetMap<Path, Integer> parsedEffectiveLines = new SetMap<>();
	private final SetMap<Path, Integer> parsedLines = new SetMap<>();
	private final SetMap<Path, Range> parsedTokens = new SetMap<>();
	private final SetMap<Path, Range> parsedNodes = new SetMap<>();
	private final Metrics metrics = new Metrics();
	private final CloneRelation relationFinder = new CloneRelation();
	private final CloneLocation locationFinder = new CloneLocation();
	private final CloneContents contentsFinder = new CloneContents();
	private final CloneRefactorability extractFinder = new CloneRefactorability();
	
	public MetricCollector() {}
	
	public void reportFoundNode(Location l) {
		metrics.incrementGeneralStatistic(Metric.LINES, StatType.TOTAL, getUnparsedLines(l, false));
		metrics.incrementGeneralStatistic(Metric.NODES, StatType.TOTAL, l.getAmountOfNodes());
		metrics.incrementGeneralStatistic(Metric.TOKENS, StatType.TOTAL, l.getAmountOfTokens());
		metrics.incrementGeneralStatistic(Metric.EFFECTIVELINES, StatType.TOTAL, getUnparsedEffectiveLines(l, false));
		l.getContents().getNodes().forEach(relationFinder::registerNode);
	}
	
	private int getUnparsedEffectiveLines(Location l, boolean countOverlap) {
		int amountOfLines = 0;
		for(Integer i : l.getContents().effectiveLines()) {
			Set<Integer> lines = parsedEffectiveLines.get(l.getFile());
			if(!lines.contains(i)) {
				amountOfLines++;
				lines.add(i);
			} else if(countOverlap) metrics.incrementGeneralStatistic(Metric.EFFECTIVELINES, StatType.OVERLAPPING, 1);
		}
		return amountOfLines;
	}
	
	private int getUnparsedLines(Location l, boolean countOverlap) {
		int amountOfLines = 0;
		for(int i = l.getRange().begin.line; i<=l.getRange().end.line; i++) {
			Set<Integer> lines = parsedLines.get(l.getFile());
			if(!lines.contains(i)) {
				amountOfLines++;
				lines.add(i);
			} else if(countOverlap) metrics.incrementGeneralStatistic(Metric.LINES, StatType.OVERLAPPING, 1);
		}
		return amountOfLines;
	}
	
	public Metrics reportClones(List<Sequence> clones) {
		parsedLines.clear();
		parsedEffectiveLines.clear();
		parsedTokens.clear();
		if(clones.isEmpty())
			metrics.generalStats.increment("Projects without clone classes");
		metrics.generalStats.increment("Clone classes", clones.size());
		metrics.averages.addTo("Amount of clone classes", clones.size());
		for(Sequence clone : clones)
			reportClone(clone);
		return metrics;
	}

	private void reportClone(Sequence clone) {
		metrics.averages.addTo("Clone class size", clone.size());
		metrics.incrementGeneralStatistic("Clone classes", 1);
		metrics.amountPerCloneClassSize.increment(clone.size());
		metrics.amountPerNodes.increment(clone.getNodeSize());
		metrics.amountPerTotalNodeVolume.increment(clone.getTotalNodeVolume());
		metrics.amountPerEffectiveLines.increment(clone.getEffectiveLineSize());
		metrics.amountPerTotalEffectiveLineVolume.increment(clone.getTotalEffectiveLineVolume());
		clone.setMetrics(relationFinder, extractFinder);
		metrics.amountPerRelation.increment(clone.getRelationType());
		metrics.amountPerExtract.increment(clone.getRefactorability());
		metrics.averages.addTo("Statements per clone class", clone.getNodeSize());
		for(Location l : clone.getLocations())
			reportClonedLocation(l);
	}

	private void reportClonedLocation(Location l) {
		metrics.averages.addTo("Clone nodes", l.getAmountOfNodes());
		metrics.averages.addTo("Clone tokens", l.getAmountOfTokens());
		metrics.averages.addTo("Clone effective lines", l.getEffectiveLines());
		metrics.incrementGeneralStatistic(Metric.LINES, StatType.CLONED, getUnparsedLines(l, true));
		metrics.incrementGeneralStatistic(Metric.TOKENS, StatType.CLONED, getUnparsedTokens(l, true));
		metrics.incrementGeneralStatistic(Metric.NODES, StatType.CLONED, getUnparsedNodes(l, true));
		metrics.incrementGeneralStatistic(Metric.EFFECTIVELINES, StatType.CLONED, getUnparsedEffectiveLines(l, true));
		l.setMetrics(locationFinder);
		l.getContents().setMetrics(contentsFinder);
		metrics.amountPerLocation.increment(l.getLocationType());
		metrics.amountPerContents.increment(l.getContents().getContentsType());
	}

	private int getUnparsedTokens(Location l, boolean countOverlap) {
		int amount = 0;
		for(JavaToken n : l.getContents().getTokens()) {
			Range r = n.getRange().get();
			if(!parsedTokens.get(l.getFile()).contains(r)) {
				parsedTokens.addTo(l.getFile(), r);
				amount++;
			} else if(countOverlap) metrics.incrementGeneralStatistic(Metric.TOKENS, StatType.OVERLAPPING, 1);
		}
		return amount;
	}

	private int getUnparsedNodes(Location l, boolean countOverlap) {
		int amount = 0;
		for(Node n : l.getContents().getNodes()) {
			Range r = n.getRange().get();
			if(!parsedNodes.get(l.getFile()).contains(r)) {
				parsedNodes.addTo(l.getFile(), r);
				amount++;
			} else if(countOverlap) metrics.incrementGeneralStatistic(Metric.NODES, StatType.OVERLAPPING, 1);
		}
		return amount;
	}

	public Metrics getMetrics() {
		return metrics;
	}

	public void reportClass(ClassOrInterfaceDeclaration firstClass) {
		relationFinder.registerClass(firstClass);
	}
}
