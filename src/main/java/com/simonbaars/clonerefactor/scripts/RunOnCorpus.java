package com.simonbaars.clonerefactor.scripts;

import java.io.File;
import java.io.IOException;

import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.thread.CalculatesTimeIntervals;
import com.simonbaars.clonerefactor.thread.ThreadPool;
import com.simonbaars.clonerefactor.thread.WritesErrors;
import com.simonbaars.clonerefactor.util.FileUtils;
import com.simonbaars.clonerefactor.util.SavePaths;

public class RunOnCorpus implements WritesErrors, CalculatesTimeIntervals {

	public static void main(String[] args) {
		new RunOnCorpus().startCorpusCloneDetection();
	}

	public Metrics startCorpusCloneDetection() {
		try {
			System.out.println(Settings.get());
			SavePaths.genTimestamp();
			System.out.println("Saving results in "+SavePaths.getMyOutputFolder());
			ThreadPool threadPool = new ThreadPool();
			File[] corpusFiles = new File(SavePaths.getApplicationDataFolder()+"git").listFiles();
			writeSettings();
			long startTime = System.currentTimeMillis();
			analyzeAllProjects(threadPool, corpusFiles);
			threadPool.finishFinalThreads();
			threadPool.getFullMetrics().generalStats.increment("Total Duration", interval(startTime));
			return threadPool.getFullMetrics();
		} catch (Exception e) {
			writeError(SavePaths.getMyOutputFolder()+"terminate", e);
		}
		return null;
	}

	private void writeSettings() {
		try {
			FileUtils.writeStringToFile(new File(SavePaths.getMyOutputFolder()+"settings.txt"), Settings.get().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void analyzeAllProjects(ThreadPool threadPool, File[] corpusFiles) {
		for(int i = 0; i<corpusFiles.length; i++) {
			System.out.println(threadPool.showContents()+" ("+(i+1)+"/"+corpusFiles.length+")");
			if(!threadPool.anyNull()) threadPool.waitForThreadToFinish();
			threadPool.addToAvailableThread(corpusFiles[i]);
		}
	}
}
