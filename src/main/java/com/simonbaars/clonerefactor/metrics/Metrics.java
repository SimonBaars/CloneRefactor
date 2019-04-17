package com.simonbaars.clonerefactor.metrics;

import com.simonbaars.clonerefactor.datatype.CountMap;

public class Metrics {
	public int totalAmountOfLines = 0;
	public int totalAmountOfNodes = 0;
	public int totalAmountOfTokens = 0;
	
	public int amountOfLinesCloned = 0;
	public int amountOfNodesCloned = 0;
	public int amountOfTokensCloned = 0;
	
	public final CountMap<Integer> amountPerCloneClassSize = new CountMap<Integer>();
}
