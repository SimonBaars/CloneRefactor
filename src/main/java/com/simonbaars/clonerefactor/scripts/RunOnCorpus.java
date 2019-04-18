package com.simonbaars.clonerefactor.scripts;

import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getFilteredCorpusFiles;
import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getJavaFiles;
import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getSourceFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.simonbaars.clonerefactor.ast.CloneParser;
import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.util.FileUtils;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

public class RunOnCorpus {
	private static File OUTPUT_FOLDER = new File("/Users/sbaars/clone/output");
	private static int NUMBER_OF_THREADS = 4;

	public static void main(String[] args) {
		CorpusThread[] threadPool = new CorpusThread[NUMBER_OF_THREADS];
		OUTPUT_FOLDER.mkdirs();
		File[] corpusFiles = getFilteredCorpusFiles(5, 1000);
		for(File file : ProgressBar.wrap(Arrays.asList(corpusFiles), new ProgressBarBuilder().setTaskName("Running Clone Detection").setStyle(ProgressBarStyle.ASCII))) {
			waitForThreadToFinish(threadPool);
			for(int i = 0; i<threadPool.length; i++) {
				if(threadPool[i]==null || !threadPool[i].isAlive()) {
					threadPool[i] = new CorpusThread(file, OUTPUT_FOLDER);
				}
			}
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
