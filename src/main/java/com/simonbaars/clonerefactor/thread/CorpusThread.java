package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.nio.file.Paths;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.model.DetectionResults;

public class CorpusThread extends Thread {
	private final File file;
	public DetectionResults res;
	public final long creationTime;
	public CorpusThreadException error;
	
	public CorpusThread(File file) {
		this.file=file;
		this.creationTime = System.currentTimeMillis();
		start();
	}
	
	public void run() {
		try {
			res = Main.cloneDetection(file.toPath(), Paths.get(file.getAbsolutePath()+"/src/main/java"));
		} catch(Exception e) {
			error = new CorpusThreadException(file, e);
		}
	}

	public void timeout() {
		interrupt();
		error = new CorpusThreadException(file, "Thread has exceeded its timeout!");
	}
	
	public File getFile() {
		return file;
	}
}
