package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.simonbaars.clonerefactor.core.util.DoesFileOperations;
import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.thread.results.DefaultResultWriter;
import com.simonbaars.clonerefactor.thread.results.WritesResults;

public class ThreadPool implements WritesErrors, CalculatesTimeIntervals, DoesFileOperations {
	private int numberOfThreads = 4;
	private final int THREAD_TIMEOUT = 60000000;
	
	private final List<Optional<CorpusThread>> threads;
	private final WritesResults resultWriter;
	
	public ThreadPool () {
		threads = new ArrayList<>(Collections.nCopies(numberOfThreads, Optional.empty()));
		this.resultWriter = new DefaultResultWriter();
	}
	
	public ThreadPool (int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
		threads = new ArrayList<>(Collections.nCopies(numberOfThreads, Optional.empty()));
		this.resultWriter = new DefaultResultWriter();
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
		if(freeMemoryPercentage()<15)
			clearThreadObjects();
	}
	
	public Optional<CorpusThread> addToAvailableThread(Settings settings, File file, File sourceRoot) {
		System.out.println(settings);
		return replaceFinishedThread(Optional.of(new CorpusThread(settings, file, sourceRoot)));
	}

	public Optional<CorpusThread> addToAvailableThread(File file) {
		return addToAvailableThread(Settings.get(), file, new File(file.getAbsolutePath()+"/src/main/java"));
	}

	private Optional<CorpusThread> replaceFinishedThread(Optional<CorpusThread> t) {
		for(int i = 0; i<threads.size(); i++) {
			if((!threads.get(i).isPresent() && t.isPresent()) || (threads.get(i).isPresent() && !threads.get(i).get().isAlive())) {
				writePreviousThreadResults(i);
				threads.set(i, t);
				return threads.get(i);
			}
		}
		throw new IllegalStateException("No thread to be replaced!");
	}
	
	private void clearThreadObjects() {
		JavaParserFacade.clearInstances();
		System.gc();
	}

	public void finishFinalThreads() {
		while(waitForThreadToFinish()) replaceFinishedThread(Optional.empty());
		resultWriter.finalize();
	}

	private void writePreviousThreadResults(int i) {
		if(threads.get(i).isPresent() && !threads.get(i).get().isAlive()) {
			if(threads.get(i).get().res != null) resultWriter.writeResults(threads.get(i).get());
			else writeError(threads.get(i).get());
		}
	}

	private void writeError(CorpusThread corpusThread) {
		writeProjectError(corpusThread.getFile().getName(), corpusThread.error);
	}
	
	public double freeMemoryPercentage() {
		return (double)Runtime.getRuntime().freeMemory() / (double)Runtime.getRuntime().totalMemory() * 100D;
	}

	public String showContents() {
		return validElements().map(e -> e.getFile().getName()).collect(Collectors.joining(", "));
	}
	
	public boolean anyNull() {
		return validElements().count() != numberOfThreads;
	}
	
	public boolean allNull() {
		return !validElements().findAny().isPresent();
	}
	
	public Metrics getFullMetrics() {
		return ((DefaultResultWriter)resultWriter).fullMetrics;
	}
}
