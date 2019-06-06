package com.simonbaars.clonerefactor.scripts.model;

import java.io.File;
import java.io.IOException;

import com.simonbaars.clonerefactor.datatype.CountTable;
import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType;
import com.simonbaars.clonerefactor.metrics.enums.CloneLocation.LocationType;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.util.FileUtils;
import com.simonbaars.clonerefactor.util.SavePaths;

public class MetricsTables {
	
	private final CountTable<String> generalStats = new CountTable<>("General Statistics");
	
	private final CountTable<RelationType> amountPerRelation = new CountTable<>("Amount per Relation");
	private final CountTable<LocationType> amountPerLocation = new CountTable<>("Amount per Location");
	private final CountTable<ContentsType> amountPerContents = new CountTable<>("Amount per Contents");
	private final CountTable<Refactorability> amountPerExtract = new CountTable<>("Amount per Extract");
	
	private final CountTable<Integer> amountPerCloneClassSize = new CountTable<>("Amount per Clone Class Size");
	private final CountTable<Integer> amountPerNodes = new CountTable<>("Amount per Nodes");
	private final CountTable<Integer> amountPerTotalNodeVolume = new CountTable<>("Amount per Total Node Volume");
	
	private final CountTable<Integer> amountPerEffectiveLines = new CountTable<>("Amount per Effective Lines");
	private final CountTable<Integer> amountPerTotalEffectiveLineVolume = new CountTable<>("Amount per Total Effective Line Volume");

	public MetricsTables() {}
	
	public void writeTables() {
		StringBuilder tableContents = new StringBuilder();
		tableContents.append(generalStats.toString());
		tableContents.append(System.lineSeparator()+System.lineSeparator());
		
		tableContents.append(amountPerRelation.toString());
		tableContents.append(System.lineSeparator()+System.lineSeparator());
		tableContents.append(amountPerLocation.toString());
		tableContents.append(System.lineSeparator()+System.lineSeparator());
		tableContents.append(amountPerContents.toString());
		tableContents.append(System.lineSeparator()+System.lineSeparator());
		tableContents.append(amountPerExtract.toString());
		tableContents.append(System.lineSeparator()+System.lineSeparator());
		
		tableContents.append(amountPerCloneClassSize.toString());
		tableContents.append(System.lineSeparator()+System.lineSeparator());
		tableContents.append(amountPerNodes.toString());
		tableContents.append(System.lineSeparator()+System.lineSeparator());
		tableContents.append(amountPerTotalNodeVolume.toString());
		tableContents.append(System.lineSeparator()+System.lineSeparator());
		
		tableContents.append(amountPerEffectiveLines.toString());
		tableContents.append(System.lineSeparator()+System.lineSeparator());
		tableContents.append(amountPerTotalEffectiveLineVolume.toString());
		tableContents.append(System.lineSeparator()+System.lineSeparator());
		try {
			FileUtils.writeStringToFile(new File(SavePaths.getMyOutputFolder()+"tables.txt"), tableContents.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void collectMetrics(String percentage, Metrics metrics) {
		generalStats.add(percentage, metrics.generalStats);
		
		amountPerRelation.add(percentage, metrics.amountPerRelation);
		amountPerLocation.add(percentage, metrics.amountPerLocation);
		amountPerContents.add(percentage, metrics.amountPerContents);
		amountPerExtract.add(percentage, metrics.amountPerExtract);
		
		amountPerCloneClassSize.add(percentage, metrics.amountPerCloneClassSize);
		amountPerNodes.add(percentage, metrics.amountPerNodes);
		amountPerTotalNodeVolume.add(percentage, metrics.amountPerTotalNodeVolume);
		
		amountPerEffectiveLines.add(percentage, metrics.amountPerEffectiveLines);
		amountPerTotalEffectiveLineVolume.add(percentage, metrics.amountPerTotalEffectiveLineVolume);
	}

}
