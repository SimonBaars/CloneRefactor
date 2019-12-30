package com.simonbaars.clonerefactor.detection.interfaces;

import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.type2.model.Type2Sequence;
import com.simonbaars.clonerefactor.settings.Settings;

public class HasSettings {
	public final Settings settings;

	public HasSettings(Settings s) {
		this.settings = s;
	}
	
	public boolean checkThresholds(Sequence s) {
		return checkSizeThreshold(s) && compareNodesThreshold(s) && compareLinesThreshold(s) && compareTokensThreshold(s);
	}

	public boolean checkSizeThreshold(HasSize s) {
		return s.size()>=settings.getMinCloneClassSize();
	}
	
	public boolean checkThresholds(Type2Sequence s) {
		return checkSizeThreshold(s) && s.getSequence().get(0).size() >= settings.getMinAmountOfNodes();
	}

	public boolean compareNodesThreshold(Sequence s) {
		return s.getAny().getNumberOfNodes() >= settings.getMinAmountOfNodes();
	}
	
	public boolean compareLinesThreshold(Sequence s) {
		return s.getAny().getNumberOfLines() >= settings.getMinAmountOfLines();
	}
	
	public boolean compareTokensThreshold(Sequence s) {
		return s.getAny().getNumberOfTokens() >= settings.getMinAmountOfTokens();
	}
	
	public boolean checkType2VariabilityThreshold(double perc) {
		return perc <= settings.getType2VariabilityPercentage();
	}
}
