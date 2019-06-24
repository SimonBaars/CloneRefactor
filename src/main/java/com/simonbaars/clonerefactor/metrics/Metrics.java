package com.simonbaars.clonerefactor.metrics;

import com.simonbaars.clonerefactor.datatype.AverageMap;
import com.simonbaars.clonerefactor.datatype.CountMap;
import com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType;
import com.simonbaars.clonerefactor.metrics.enums.CloneLocation.LocationType;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.metrics.enums.Metric;
import com.simonbaars.clonerefactor.metrics.enums.StatType;

public class Metrics {
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

	@Override
	public String toString() {
		return String.format(
				"Metrics [generalStats=%s, amountPerRelation=%s, amountPerLocation=%s, amountPerContents=%s, amountPerExtract=%s, amountPerCloneClassSize=%s, amountPerNodes=%s, amountPerTotalNodeVolume=%s, amountPerEffectiveLines=%s, amountPerTotalEffectiveLineVolume=%s]",
				generalStats, amountPerRelation, amountPerLocation, amountPerContents, amountPerExtract,
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
	}

	public void incrementGeneralStatistic(Metric metric, StatType type, int amount) {
		generalStats.increment(type.toString()+" "+metric.toString(), amount);
	}
	
	public void incrementGeneralStatistic(String generalStat, int amount) {
		generalStats.increment(generalStat, amount);
	}
}
