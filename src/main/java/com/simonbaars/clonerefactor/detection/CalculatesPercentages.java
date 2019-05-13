package com.simonbaars.clonerefactor.detection;

public interface CalculatesPercentages {
	public default double calcPercentage(int part, int whole) {
		return (double)part/(double)whole*100D;
	}
}
