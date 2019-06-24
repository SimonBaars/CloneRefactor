package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.util.FileUtils;
import com.simonbaars.clonerefactor.util.SavePaths;

public class ThreadPool implements WritesErrors {
	private final File OUTPUT_FOLDER = new File(SavePaths.getFullOutputFolder());
	private final File FULL_METRICS = new File(OUTPUT_FOLDER.getParent()+"/metrics.txt");
	private final int NUMBER_OF_THREADS = 4;
	private final int THREAD_TIMEOUT = 600000;
	private final Metrics fullMetrics = new Metrics();
	
	private final CorpusThread[] threads;
	
	public ThreadPool () {
		threads = new CorpusThread[NUMBER_OF_THREADS];
		OUTPUT_FOLDER.mkdirs();
	}

	public void waitForThreadToFinish() {
		if(allNull())
			return;
		while(Arrays.stream(threads).filter(Objects::nonNull).noneMatch(e -> !e.isAlive())) {
			try {
				Thread.sleep(100);
				nullifyThreadIfStarved();
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private void nullifyThreadIfStarved() {
		IntStream.range(0,size()).filter(i -> threads[i]!=null && threads[i].creationTime+THREAD_TIMEOUT<System.currentTimeMillis()).forEach(i -> {
			threads[i].timeout();
		});
	}

	private int size() {
		return NUMBER_OF_THREADS;
	}

	public void addToAvailableThread(File file) {
		for(int i = 0; i<threads.length; i++) {
			if(threads[i]==null || !threads[i].isAlive()) {
				writePreviousThreadResults(i);
				threads[i] = new CorpusThread(file);
				break;
			}
		}
	}
	
	public void finishFinalThreads() {
		while(Arrays.stream(threads).anyMatch(Objects::nonNull)) {
			waitForThreadToFinish();
			for(int i = 0; i<threads.length; i++) {
				if(threads[i] != null && !threads[i].isAlive()) {
					writePreviousThreadResults(i);
					threads[i] = null;
				}
			}
		}
	}

	private void writePreviousThreadResults(int i) {
		if(threads[i]!=null && !threads[i].isAlive()) {
			if(threads[i].res != null)
				writeResults(threads[i]);
			else writeError(i);
			if(freeMemoryPercentage()<15) JavaParserFacade.clearInstances();
			threads[i]=null;
		}
	}

	private void writeError(int i) {
		writeProjectError(threads[i].getFile().getName(), threads[i].error);
	}

	private void writeResults(CorpusThread t) {
		calculateGeneralMetrics(t);
		fullMetrics.add(t.res.getMetrics());
		try {
			FileUtils.writeStringToFile(new File(OUTPUT_FOLDER.getAbsolutePath()+File.separator+t.getFile().getName()+"-"+t.res.getClones().size()+".txt"), t.res.toString());
			FileUtils.writeStringToFile(FULL_METRICS, fullMetrics.toString());
		} catch (IOException e) {
			writeProjectError(t.getFile().getName(), e);
		}
	}

	private void calculateGeneralMetrics(CorpusThread t) {
		t.res.getMetrics().generalStats.increment("Duration", Math.toIntExact(System.currentTimeMillis()-t.creationTime));
	}
	
	public double freeMemoryPercentage() {
		return (double)Runtime.getRuntime().freeMemory() / (double)Runtime.getRuntime().totalMemory() * 100D;
	}

	public String showContents() {
		return Arrays.stream(threads).filter(Objects::nonNull).map(e -> e.getFile().getName()).collect(Collectors.joining(", "));
	}
	
	public boolean anyNull() {
		return Arrays.stream(threads).anyMatch(Objects::nonNull);
	}
	
	public boolean allNull() {
		return Arrays.stream(threads).allMatch(Objects::nonNull);
	}
	
	public Metrics getFullMetrics() {
		return fullMetrics;
	}
}
