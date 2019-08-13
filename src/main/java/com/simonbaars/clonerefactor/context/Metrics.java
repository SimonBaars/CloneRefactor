package com.simonbaars.clonerefactor.context;

import java.util.Optional;

import com.simonbaars.clonerefactor.context.context.Metric;
import com.simonbaars.clonerefactor.context.context.StatType;
import com.simonbaars.clonerefactor.context.context.enums.ContentsType;
import com.simonbaars.clonerefactor.context.context.enums.LocationType;
import com.simonbaars.clonerefactor.context.context.enums.Refactorability;
import com.simonbaars.clonerefactor.context.context.enums.RelationType;
import com.simonbaars.clonerefactor.context.context.enums.Risk;
import com.simonbaars.clonerefactor.datatype.map.AverageMap;
import com.simonbaars.clonerefactor.datatype.map.CountMap;

public class Metrics {
	private Optional<Metrics> child = Optional.empty(); 
	
	public final CountMap<String> generalStats = new CountMap<>();
	public final AverageMap<String> averages = new AverageMap<>();

	public final CountMap<RelationType> amountPerRelation = new CountMap<>();
	public final CountMap<LocationType> amountPerLocation = new CountMap<>();
	public final CountMap<ContentsType> amountPerContents = new CountMap<>();
	public final CountMap<Refactorability> amountPerExtract = new CountMap<>();
	
	public final CountMap<Integer> amountPerCloneClassSize = new CountMap<>();
	public final CountMap<Integer> amountPerNodes = new CountMap<>();
	public final CountMap<Integer> amountPerTotalNodeVolume = new CountMap<>();
	
	public final CountMap<Integer> amountPerEffectiveLines = new CountMap<>();
	public final CountMap<Integer> amountPerTotalEffectiveLineVolume = new CountMap<>();
	
	public final CountMap<String> riskProfiles = new CountMap<>();

	@Override
	public String toString() {
		return String.format(
				"Metrics [generalStats=%s, riskProfiles=%s, averages=%s, amountPerRelation=%s, amountPerLocation=%s, amountPerContents=%s, amountPerExtract=%s, amountPerCloneClassSize=%s, amountPerNodes=%s, amountPerTotalNodeVolume=%s, amountPerEffectiveLines=%s, amountPerTotalEffectiveLineVolume=%s]",
				generalStats, riskProfiles, averages, amountPerRelation, amountPerLocation, amountPerContents, amountPerExtract,
				amountPerCloneClassSize, amountPerNodes, amountPerTotalNodeVolume, amountPerEffectiveLines,
				amountPerTotalEffectiveLineVolume);
	}

	public void add(Metrics metrics) {
		generalStats.addAll(metrics.generalStats);
		averages.addAll(metrics.averages);
		
		amountPerRelation.addAll(metrics.amountPerRelation);
		amountPerLocation.addAll(metrics.amountPerLocation);
		amountPerContents.addAll(metrics.amountPerContents);
		amountPerExtract.addAll(metrics.amountPerExtract);
		
		amountPerCloneClassSize.addAll(metrics.amountPerCloneClassSize);
		amountPerNodes.addAll(metrics.amountPerNodes);
		amountPerTotalNodeVolume.addAll(metrics.amountPerTotalNodeVolume);
		
		amountPerEffectiveLines.addAll(metrics.amountPerEffectiveLines);
		amountPerTotalEffectiveLineVolume.addAll(metrics.amountPerTotalEffectiveLineVolume);
		
		riskProfiles.addAll(metrics.riskProfiles);
	}

	public void incrementGeneralStatistic(Metric metric, StatType type, int amount) {
		incrementGeneralStatistic(type+" "+metric, amount);
	}
	
	public void incrementGeneralStatistic(String generalStat, int amount) {
		generalStats.increment(generalStat, amount);
	}
	
	public void incrementGeneralStatistic(ProblemType metric, Risk type) {
		riskProfiles.increment(metric+" "+type+" Risk", 1);
	}
	
	public void incrementGeneralStatistic(ProblemType metric, int score) {
		incrementGeneralStatistic(metric, metric.getRisk(score));
	}
	
	public void setChild(Metrics results) {
		child = Optional.of(results);
	}

	public Optional<Metrics> getChild() {
		return child;
	}
}
