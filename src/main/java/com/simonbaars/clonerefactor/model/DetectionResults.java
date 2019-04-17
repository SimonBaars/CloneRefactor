package com.simonbaars.clonerefactor.model;

import java.util.List;

import com.simonbaars.clonerefactor.metrics.Metrics;

public class DetectionResults {
	private Metrics metrics;
	private List<Sequence> clones;
	
	public DetectionResults(Metrics metrics, List<Sequence> clones) {
		super();
		this.metrics = metrics;
		this.clones = clones;
	}
	
	public Metrics getMetrics() {
		return metrics;
	}
	
	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}
	
	public List<Sequence> getClones() {
		return clones;
	}
	
	public void setClones(List<Sequence> clones) {
		this.clones = clones;
	}
}
