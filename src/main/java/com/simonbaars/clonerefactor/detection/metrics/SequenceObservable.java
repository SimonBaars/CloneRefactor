package com.simonbaars.clonerefactor.detection.metrics;

import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.context.ProblemType;
import com.simonbaars.clonerefactor.detection.metrics.interfaces.SequenceObserver;
import com.simonbaars.clonerefactor.detection.model.Sequence;

public class SequenceObservable {
	private final List<SequenceObserver> observers = new ArrayList<>();
	
	public void sendUpdate(ProblemType problem, Sequence sequence, int problemSize) {
		observers.forEach(e -> e.update(problem, sequence, problemSize));
	}
	
	public boolean isActive() {
		return !observers.isEmpty();
	}

	public SequenceObservable subscribe(SequenceObserver observer) {
		observers.add(observer);
		return this;
	}

	public void unsubscribe(SequenceObserver observer) {
		observers.remove(observer);
	}
}
