package com.simonbaars.clonerefactor.scripts.model;

import java.io.File;
import java.io.IOException;

import com.simonbaars.clonerefactor.datatype.map.CountTable;
import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.thread.WritesErrors;
import com.simonbaars.clonerefactor.util.SavePaths;

public class MetricsTables implements WritesErrors {
	
	private final CountTable generalStats = new CountTable("General Statistics");
	private final CountTable averages = new CountTable("Averages");
	
	private final CountTable amountPerRelation = new CountTable("Amount per Relation");
	private final CountTable amountPerLocation = new CountTable("Amount per Location");
	private final CountTable amountPerContents = new CountTable("Amount per Contents");
	private final CountTable amountPerExtract = new CountTable("Amount per Extract");
	
	private final CountTable amountPerCloneClassSize = new CountTable("Amount per Clone Class Size");
	private final CountTable amountPerNodes = new CountTable("Amount per Nodes");
	private final CountTable amountPerTotalNodeVolume = new CountTable("Amount per Total Node Volume");
	
	private final CountTable amountPerEffectiveLines = new CountTable("Amount per Effective Lines");
	private final CountTable amountPerTotalEffectiveLineVolume = new CountTable("Amount per Total Effective Line Volume");
	
	public void writeTables() {
		StringBuilder tableContents = new StringBuilder();
		tableContents.append(generalStats.toString());
		tableContents.append(System.lineSeparator()+System.lineSeparator());
		tableContents.append(averages.toString());
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
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"tables.txt"), tableContents.toString());
		} catch (IOException e) {
			writeProjectError("0_tables", e);
		}
	}

	public void collectMetrics(String percentage, Metrics metrics) {
		generalStats.put(percentage, metrics.generalStats);
		averages.put(percentage, metrics.averages);
		
		amountPerRelation.put(percentage, metrics.amountPerRelation);
		amountPerLocation.put(percentage, metrics.amountPerLocation);
		amountPerContents.put(percentage, metrics.amountPerContents);
		amountPerExtract.put(percentage, metrics.amountPerExtract);
		
		amountPerCloneClassSize.put(percentage, metrics.amountPerCloneClassSize);
		amountPerNodes.put(percentage, metrics.amountPerNodes);
		amountPerTotalNodeVolume.put(percentage, metrics.amountPerTotalNodeVolume);
		
		amountPerEffectiveLines.put(percentage, metrics.amountPerEffectiveLines);
		amountPerTotalEffectiveLineVolume.put(percentage, metrics.amountPerTotalEffectiveLineVolume);
	}

}
