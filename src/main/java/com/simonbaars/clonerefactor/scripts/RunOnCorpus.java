package com.simonbaars.clonerefactor.scripts;

import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getFilteredCorpusFiles;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.util.FileUtils;
import com.simonbaars.clonerefactor.util.SavePaths;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;

public class RunOnCorpus {
	private static File OUTPUT_FOLDER = new File(SavePaths.getFullOutputFolder());
	private static File FULL_METRICS = new File(OUTPUT_FOLDER.getParent()+"/full_metrics.txt");
	private static int NUMBER_OF_THREADS = 1;
	private static final Metrics fullMetrics = new Metrics();

	public static void main(String[] args) {
		CorpusThread[] threadPool = new CorpusThread[NUMBER_OF_THREADS];
		OUTPUT_FOLDER.mkdirs();
		File[] corpusFiles = getFilteredCorpusFiles(0, 1000);
		analyzeAllProjects(threadPool, corpusFiles);
		System.out.println("Finishing up :)");
		finishFinalThreads(threadPool);
		System.out.println("Done! All results were written to "+OUTPUT_FOLDER+".");
	}

	private static void analyzeAllProjects(CorpusThread[] threadPool, File[] corpusFiles) {
		for(File file : ProgressBar.wrap(Arrays.asList(corpusFiles), new ProgressBarBuilder().setTaskName("Running Clone Detection"))) {
			System.out.println("Start "+file);
			waitForThreadToFinish(threadPool);
			for(int i = 0; i<threadPool.length; i++) {
				if(threadPool[i]==null || !threadPool[i].isAlive()) {
					enableNewThread(threadPool, file, i);
					break;
				}
			}
		}
	}

	private static void finishFinalThreads(CorpusThread[] threadPool) {
		while(Arrays.stream(threadPool).anyMatch(e -> e!=null)) {
			waitForThreadToFinish(threadPool);
			for(int i = 0; i<threadPool.length; i++) {
				if(threadPool[i] != null && !threadPool[i].isAlive()) {
					threadPool[i] = null;
				}
			}
		}
	}

	private static void enableNewThread(CorpusThread[] threadPool, File file, int i) {
		writePreviousThreadResults(threadPool, file, i);
		threadPool[i] = new CorpusThread(file);
	}

	private static void writePreviousThreadResults(CorpusThread[] threadPool, File file, int i) {
		if(threadPool[i]!=null && !threadPool[i].isAlive()) {
			if(threadPool[i].res != null)
				writeResults(file, threadPool[i].res);
			else fullMetrics.skipped++;
			threadPool[i]=null;
		}
	}

	private static void writeResults(File file, DetectionResults res) {
		fullMetrics.add(res.getMetrics());
		try {
			FileUtils.writeStringToFile(new File(OUTPUT_FOLDER.getAbsolutePath()+"/"+file.getName()+"-"+res.getClones().size()+".txt"), res.toString());
			FileUtils.writeStringToFile(FULL_METRICS, fullMetrics.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void waitForThreadToFinish(CorpusThread[] threadPool) {
		while(Arrays.stream(threadPool).allMatch(e -> e!=null && e.isAlive())) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
