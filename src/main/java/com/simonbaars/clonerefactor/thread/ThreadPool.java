package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.util.FileUtils;
import com.simonbaars.clonerefactor.util.SavePaths;

public class ThreadPool {
	private static File OUTPUT_FOLDER = new File(SavePaths.getFullOutputFolder());
	private static File FULL_METRICS = new File(OUTPUT_FOLDER.getParent()+"/full_metrics.txt");
	private static int NUMBER_OF_THREADS = 4;
	private static int THREAD_TIMEOUT = 100000;
	private static final Metrics fullMetrics = new Metrics();
	
	private final CorpusThread[] threads;
	
	public ThreadPool (int nThreads) {
		threads = new Thread[nThreads];
		OUTPUT_FOLDER.mkdirs();
	}

	public void waitForThreadToFinish() {
		while(Arrays.stream(threads).allMatch(e -> e!=null && e.isAlive())) {
			try {
				Thread.sleep(100);
				IntStream.range(0,size()).filter(i -> threads[i].creationTime+THREAD_TIMEOUT<System.currentTimeMillis()).forEach(i -> {
					threads[i].interrupt(); threads[i] = null;
				});
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
		}
	}

	private int size() {
		return NUMBER_OF_THREADS;
	}

	public void addToAvailableThread(File file) {
		for(int i = 0; i<threads.length; i++) {
			if(threads[i]==null || !threads[i].isAlive()) {
				enableNewThread(file, i);
				break;
			}
		}
	}
	
	public void finishFinalThreads(CorpusThread[] threadPool) {
		while(Arrays.stream(threadPool).anyMatch(e -> e!=null)) {
			waitForThreadToFinish();
			for(int i = 0; i<threadPool.length; i++) {
				if(threadPool[i] != null && !threadPool[i].isAlive()) {
					threadPool[i] = null;
				}
			}
		}
	}

	private void enableNewThread(File file, int i) {
		writePreviousThreadResults(threads, file, i);
		threads[i] = new CorpusThread(file);
	}

	private void writePreviousThreadResults(File file, int i) {
		if(threads[i]!=null && !threads[i].isAlive()) {
			if(threads[i].res != null)
				writeResults(file, threads[i].res);
			else fullMetrics.skipped++;
			threads[i]=null;
		}
	}

	private void writeResults(Metrics fullMetrics, File file, DetectionResults res) {
		fullMetrics.add(res.getMetrics());
		try {
			FileUtils.writeStringToFile(new File(OUTPUT_FOLDER.getAbsolutePath()+"/"+file.getName()+"-"+res.getClones().size()+".txt"), res.toString());
			FileUtils.writeStringToFile(FULL_METRICS, fullMetrics.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
