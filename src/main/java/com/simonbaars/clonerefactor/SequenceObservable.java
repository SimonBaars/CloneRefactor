package com.simonbaars.clonerefactor;

import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.metrics.ProblemType;
import com.simonbaars.clonerefactor.model.Sequence;

public class SequenceObservable {
	private final List<SequenceObserver> observers = new ArrayList<>();
	
	public void sendUpdate(ProblemType problem, Sequence sequence, int problemSize) {
		observers.forEach(e -> e.update(problem, sequence, problemSize));
	}
	
	public boolean isActive() {
		return !observers.isEmpty();
	}

	public void subscribe(SequenceObserver observer) {
		observers.add(observer);
	}

	public void unsubscribe(SequenceObserver observer) {
		observers.remove(observer);
	}
}
