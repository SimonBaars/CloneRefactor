package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.simonbaars.clonerefactor.datatype.map.SimpleTable;
import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.util.DoesFileOperations;
import com.simonbaars.clonerefactor.util.SavePaths;

public class ThreadPool implements WritesErrors, CalculatesTimeIntervals, DoesFileOperations {
	private final File OUTPUT_FOLDER = new File(SavePaths.getFullOutputFolder());
	private final File FULL_METRICS = new File(OUTPUT_FOLDER.getParent()+"/metrics.txt");
	private final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors();
	private final int THREAD_TIMEOUT = 6000000;
	private final Metrics fullMetrics = new Metrics();
	private final SimpleTable refactorResults = new SimpleTable("System", "Nodes", "Tokens", "Relation", "Returns", "Arguments", "Duplication", "Complexity", "Interface Size", "Size", "Duplication (nodes)", "Size (nodes)");
	
	private final List<Optional<CorpusThread>> threads;
	
	public ThreadPool () {
		threads = new ArrayList<>(Collections.nCopies(NUMBER_OF_THREADS, Optional.empty()));
		OUTPUT_FOLDER.mkdirs();
	}

	public boolean waitForThreadToFinish() {
		if(allNull())
			return false;
		while(validElements().noneMatch(e -> !e.isAlive())) {
			try {
				Thread.sleep(100);
				nullifyThreadIfStarved();
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
		}
		return true;
	}
	
	public Stream<CorpusThread> validElements(){
		return validElements(threads);
	}
	
	public<T> Stream<T> validElements(List<Optional<T>> list){
		return validElements(list.stream());
	}	
	
	public<T> Stream<T> validElements(Stream<Optional<T>> stream){
		return stream.filter(Optional::isPresent).map(Optional::get);
	}

	private void nullifyThreadIfStarved() {
		validElements().filter(i -> i.creationTime+THREAD_TIMEOUT<System.currentTimeMillis()).forEach(CorpusThread::timeout);
	}

	public void addToAvailableThread(File file) {
		replaceFinishedThread(Optional.of(new CorpusThread(file)));
	}

	private void replaceFinishedThread(Optional<CorpusThread> t) {
		for(int i = 0; i<threads.size(); i++) {
			if((!threads.get(i).isPresent() && t.isPresent()) || (threads.get(i).isPresent() && !threads.get(i).get().isAlive())) {
				writePreviousThreadResults(i);
				threads.set(i, t);
				break;
			}
		}
	}
	
	public void finishFinalThreads() {
		while(waitForThreadToFinish()) replaceFinishedThread(Optional.empty());
		try {
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"refactor.txt"), refactorResults.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writePreviousThreadResults(int i) {
		if(threads.get(i).isPresent() && !threads.get(i).get().isAlive()) {
			if(threads.get(i).get().res != null) writeResults(threads.get(i).get());
			else writeError(threads.get(i).get());
			if(freeMemoryPercentage()<15) JavaParserFacade.clearInstances();
		}
	}

	private void writeError(CorpusThread corpusThread) {
		writeProjectError(corpusThread.getFile().getName(), corpusThread.error);
	}

	private void writeResults(CorpusThread t) {
		calculateGeneralMetrics(t);
		fullMetrics.add(t.res.getMetrics());
		refactorResults.addAll(t.res.getRefactorResults());
		try {
			writeStringToFile(new File(OUTPUT_FOLDER.getAbsolutePath()+File.separator+t.getFile().getName()+"-"+t.res.getClones().size()+".txt"), t.res.toString());
			writeStringToFile(FULL_METRICS, fullMetrics.toString());
		} catch (IOException e) {
			writeProjectError(t.getFile().getName(), e);
		}
	}

	private void calculateGeneralMetrics(CorpusThread t) {
		int duration = interval(t.creationTime);
		t.res.getMetrics().generalStats.increment("Duration", duration);
		t.res.getMetrics().averages.addTo("Average duration", duration);
	}
	
	public double freeMemoryPercentage() {
		return (double)Runtime.getRuntime().freeMemory() / (double)Runtime.getRuntime().totalMemory() * 100D;
	}

	public String showContents() {
		return validElements().map(e -> e.getFile().getName()).collect(Collectors.joining(", "));
	}
	
	public boolean anyNull() {
		return validElements().count() != NUMBER_OF_THREADS;
	}
	
	public boolean allNull() {
		return !validElements().findAny().isPresent();
	}
	
	public Metrics getFullMetrics() {
		return fullMetrics;
	}
}
