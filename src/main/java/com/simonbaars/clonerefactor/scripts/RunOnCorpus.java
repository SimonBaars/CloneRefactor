package com.simonbaars.clonerefactor.scripts;

import java.io.File;
import java.util.Arrays;

import com.simonbaars.clonerefactor.thread.ThreadPool;
import com.simonbaars.clonerefactor.util.SavePaths;

import me.tongfei.progressbar.ProgressBar;

public class RunOnCorpus {

	public static void main(String[] args) {
		ThreadPool threadPool = new ThreadPool();
		
		File[] corpusFiles = new File(SavePaths.getApplicationDataFolder()+"git").listFiles();
		analyzeAllProjects(threadPool, corpusFiles);
		threadPool.finishFinalThreads();
	}

	private static void analyzeAllProjects(ThreadPool threadPool, File[] corpusFiles) {
		for(File file : ProgressBar.wrap(Arrays.asList(corpusFiles), "Running Clone Detection")) {
			threadPool.waitForThreadToFinish();
			threadPool.addToAvailableThread(file);
		}
	}
}
