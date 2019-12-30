package com.simonbaars.clonerefactor.detection.type2.model;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;

public class WeightedPercentage implements CalculatesPercentages {
	private final double percentage;
	private final int weight;
	
	public WeightedPercentage(double percentage, int weight) {
		super();
		this.percentage = percentage;
		this.weight = weight;
	}

	public double getPercentage() {
		return percentage;
	}

	public int getWeight() {
		return weight;
	}

	public WeightedPercentage mergeWith(WeightedPercentage weighedPercentage) {
		int total = weight+weighedPercentage.getWeight();
		double combinedPercentage = unweighted(total) + weighedPercentage.unweighted(total);
		return new WeightedPercentage(combinedPercentage, total);
	}

	private double unweighted(int total) {
		return total == 0 ? 0D : percentage/total*weight;
	}

	@Override
	public String toString() {
		DecimalFormat formatter = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance( Locale.ENGLISH ));
		formatter.setRoundingMode( RoundingMode.HALF_UP );
		return formatter.format(percentage);
	}
}