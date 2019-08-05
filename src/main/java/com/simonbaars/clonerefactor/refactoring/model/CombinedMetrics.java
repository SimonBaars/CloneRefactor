package com.simonbaars.clonerefactor.refactoring.model;

import com.simonbaars.clonerefactor.metrics.MetricCollector;

public class CombinedMetrics {
	private final int ccIncrease;
	private final int lineSizeIncrease;
	private final int tokenSizeIncrease;
	private final int nodeSizeIncrease;
	private final int unitInterfaceSizeIncrease;
	
	public CombinedMetrics(int ccIncrease, int lineSizeIncrease, int tokenSizeIncrease, int nodeSizeIncrease,
			int unitInterfaceSizeIncrease) {
		super();
		this.ccIncrease = ccIncrease;
		this.lineSizeIncrease = lineSizeIncrease;
		this.tokenSizeIncrease = tokenSizeIncrease;
		this.nodeSizeIncrease = nodeSizeIncrease;
		this.unitInterfaceSizeIncrease = unitInterfaceSizeIncrease;
	}

	public int getCcIncrease() {
		return ccIncrease;
	}

	public int getLineSizeIncrease() {
		return lineSizeIncrease;
	}

	public int getTokenSizeIncrease() {
		return tokenSizeIncrease;
	}

	public int getNodeSizeIncrease() {
		return nodeSizeIncrease;
	}

	public int getUnitInterfaceSizeIncrease() {
		return unitInterfaceSizeIncrease;
	}
	
	public void save(MetricCollector collector) {
		collector.getMetrics().incrementGeneralStatistic("CC Increase", ccIncrease);
		collector.getMetrics().incrementGeneralStatistic("Lines Increase", lineSizeIncrease);
		collector.getMetrics().incrementGeneralStatistic("Tokens Increase", tokenSizeIncrease);
		collector.getMetrics().incrementGeneralStatistic("Nodes Increase", nodeSizeIncrease);
		collector.getMetrics().incrementGeneralStatistic("Unit Size Increase", unitInterfaceSizeIncrease);
	}
	
	
	public String createString(PreMetrics pre, PostMetrics post) {
		return "This refactoring has the following effects on system quality metrics:"+System.lineSeparator()+
				tellWhatHappened("Total Cyclomatic Complexity", pre.getCc(), post.getCc()) +
				tellWhatHappened("Total Lines", pre.getLines(), post.getAddedLineVolume());
	}
	
	private String tellWhatHappened(String metric, int oldValue, int newValue) {
		StringBuilder stringBuilder = new StringBuilder(metric+" ");
		if(oldValue == newValue) {
			stringBuilder.append("did not change and is still "+oldValue);
		} else {
			if(oldValue>newValue) {
				stringBuilder.append("increased");
			} else {
				stringBuilder.append("decreased");
			}
			stringBuilder.append(" by "+Math.abs(newValue - oldValue)+" from "+oldValue+" to "+newValue+".");
		}
		stringBuilder.append("."+System.lineSeparator());
		return stringBuilder.toString();
	}
}
