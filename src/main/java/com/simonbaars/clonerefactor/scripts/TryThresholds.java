package com.simonbaars.clonerefactor.scripts;

import java.io.File;
import java.io.IOException;

import com.simonbaars.clonerefactor.datatype.CountTable;
import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType;
import com.simonbaars.clonerefactor.metrics.enums.CloneLocation.LocationType;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.util.FileUtils;
import com.simonbaars.clonerefactor.util.SavePaths;

public class TryThresholds implements Runnable {
	final CountTable<RelationType> amountPerRelation = new CountTable<>();
	final CountTable<LocationType> amountPerLocation = new CountTable<>();
	final CountTable<ContentsType> amountPerContents = new CountTable<>();
	final CountTable<Refactorability> amountPerExtract = new CountTable<>();
	
	final CountTable<Integer> amountPerCloneClassSize = new CountTable<>();
	final CountTable<Integer> amountPerNodes = new CountTable<>();
	final CountTable<Integer> amountPerTotalNodeVolume = new CountTable<>();
	
	final CountTable<Integer> amountPerEffectiveLines = new CountTable<>();
	final CountTable<Integer> amountPerTotalEffectiveLineVolume = new CountTable<>();
	
	public static void main(String[] args) {
		new TryThresholds().run();
	}

	@Override
	public void run() {
		System.out.println("Try Thresholds");
		for(int i = 1; i<150; i++) {
			Settings.get().setMinAmountOfTokens(i);
			Metrics metrics = new RunOnCorpus().startCorpusCloneDetection();
			if(metrics != null)
				collectMetrics(metrics);
			writeTables();
		}
	}

	private void writeTables() {
		StringBuilder tableContents = new StringBuilder();
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

	private void collectMetrics(Metrics metrics) {
		amountPerRelation.add(metrics.amountPerRelation);
		amountPerLocation.add(metrics.amountPerLocation);
		amountPerContents.add(metrics.amountPerContents);
		amountPerExtract.add(metrics.amountPerExtract);
		
		amountPerCloneClassSize.add(metrics.amountPerCloneClassSize);
		amountPerNodes.add(metrics.amountPerNodes);
		amountPerTotalNodeVolume.add(metrics.amountPerTotalNodeVolume);
		
		amountPerEffectiveLines.add(metrics.amountPerEffectiveLines);
		amountPerTotalEffectiveLineVolume.add(metrics.amountPerTotalEffectiveLineVolume);
	}
}
