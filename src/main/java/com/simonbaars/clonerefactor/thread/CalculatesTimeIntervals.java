package com.simonbaars.clonerefactor.thread;

public interface CalculatesTimeIntervals {
	public default int interval(long beginTime) {
		return Math.toIntExact(System.currentTimeMillis()-beginTime);
	}
}
