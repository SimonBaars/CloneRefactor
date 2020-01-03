package com.simonbaars.clonerefactor.scripts;

import java.io.File;

import com.simonbaars.clonerefactor.core.util.DoesFileOperations;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.thread.CalculatesTimeIntervals;
import com.simonbaars.clonerefactor.thread.ThreadPool;
import com.simonbaars.clonerefactor.thread.WritesErrors;
import com.simonbaars.clonerefactor.thread.results.IntimalsResultWriter;

public class RunIntimals implements DoesFileOperations, WritesErrors, CalculatesTimeIntervals {

	public static void main(String[] args) {
		new RunIntimals().run();
	}
	
	public void run() {
		ThreadPool threadPool = new ThreadPool(new IntimalsResultWriter());
		for(double gapSize = 0D; gapSize<=1000D; gapSize+=20D) {
			for(int minLines = 1; minLines<=10; minLines++) {
				System.out.println(gapSize+", "+minLines);
				if(!threadPool.anyNull()) threadPool.waitForThreadToFinish();
				threadPool.addToAvailableThread(Settings.builder().withMinAmountOfLines(minLines).withType3GapSize(gapSize).build(), new File("/Users/sbaars/Documents/Kim/jhotdraw/"), new File("/Users/sbaars/Documents/Kim/jhotdraw/src/"));	
			}
		}
		threadPool.finishFinalThreads();
	}
}
