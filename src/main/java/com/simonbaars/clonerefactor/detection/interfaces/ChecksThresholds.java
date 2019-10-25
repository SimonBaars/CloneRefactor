package com.simonbaars.clonerefactor.detection.interfaces;

import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.settings.Settings;

public interface ChecksThresholds {
	public default boolean checkThresholds(Sequence s) {
		return compareNodesThreshold(s) && compareLinesThreshold(s) && compareTokensThreshold(s);
	}

	public default boolean compareNodesThreshold(Sequence s) {
		return s.getAny().getNumberOfNodes() >= Settings.get().getMinAmountOfNodes();
	}
	
	public default boolean compareLinesThreshold(Sequence s) {
		return s.getAny().getNumberOfLines() >= Settings.get().getMinAmountOfLines();
	}
	
	public default boolean compareTokensThreshold(Sequence s) {
		return s.getAny().getNumberOfTokens() >= Settings.get().getMinAmountOfTokens();
	}
	
	public default boolean checkType2VariabilityThreshold(double perc) {
		return perc <= Settings.get().getType2VariabilityPercentage();
	}
}
