package com.simonbaars.clonerefactor.metrics;

import com.simonbaars.clonerefactor.datatype.CountMap;

public class Metrics {
	public int totalAmountOfLines = 0;
	public int totalAmountOfEffectiveLines = 0;
	public int totalAmountOfNodes = 0;
	public int totalAmountOfTokens = 0;
	
	public int amountOfLinesCloned = 0;
	public int amountOfEffectiveLinesCloned = 0;
	public int amountOfNodesCloned = 0;
	public int amountOfTokensCloned = 0;
	
	public final CountMap<Integer> amountPerCloneClassSize = new CountMap<>();
	public final CountMap<CloneRelation> amountPerLocation = new CountMap<>();
	
	public final CountMap<Integer> amountPerNodes = new CountMap<>();
	public final CountMap<Integer> amountPerTotalNodeVolume = new CountMap<>();
	
	public final CountMap<Integer> amountPerEffectiveLines = new CountMap<>();
	public final CountMap<Integer> amountPerTotalEffectiveLineVolume = new CountMap<>();
	
	@Override
	public String toString() {
		return "Metrics [totalAmountOfLines=" + totalAmountOfLines + ", totalAmountOfEffectiveLines="
				+ totalAmountOfEffectiveLines + ", totalAmountOfNodes=" + totalAmountOfNodes + ", totalAmountOfTokens="
				+ totalAmountOfTokens + ", amountOfLinesCloned=" + amountOfLinesCloned
				+ ", amountOfEffectiveLinesCloned=" + amountOfEffectiveLinesCloned + ", amountOfNodesCloned="
				+ amountOfNodesCloned + ", amountOfTokensCloned=" + amountOfTokensCloned + ", amountPerCloneClassSize="
				+ amountPerCloneClassSize + ", amountPerLocation=" + amountPerLocation + ", amountPerNodes="
				+ amountPerNodes + ", amountPerTotalNodeVolume=" + amountPerTotalNodeVolume
				+ ", amountPerEffectiveLines=" + amountPerEffectiveLines + ", amountPerTotalEffectiveLineVolume="
				+ amountPerTotalEffectiveLineVolume + "]";
	}

	public void add(Metrics metrics) {
		totalAmountOfLines+=metrics.totalAmountOfLines;
		totalAmountOfNodes+=metrics.totalAmountOfNodes;
		totalAmountOfTokens+=metrics.totalAmountOfTokens;
		totalAmountOfEffectiveLines+=metrics.totalAmountOfEffectiveLines;
		
		amountOfLinesCloned+=metrics.amountOfLinesCloned;
		amountOfNodesCloned+=metrics.amountOfNodesCloned;
		amountOfTokensCloned+=metrics.amountOfTokensCloned;
		amountOfEffectiveLinesCloned+=metrics.amountOfEffectiveLinesCloned;
		
		amountPerCloneClassSize.addAll(metrics.amountPerCloneClassSize);
		amountPerLocation.addAll(metrics.amountPerLocation);
		
		amountPerNodes.addAll(metrics.amountPerNodes);
		amountPerTotalNodeVolume.addAll(metrics.amountPerTotalNodeVolume);
		
		amountPerEffectiveLines.addAll(metrics.amountPerEffectiveLines);
		amountPerTotalEffectiveLineVolume.addAll(metrics.amountPerTotalEffectiveLineVolume);
	}

	
	
	
}
