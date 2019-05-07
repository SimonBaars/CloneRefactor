package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.nio.file.Paths;
import java.util.Vector;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.model.DetectionResults;

public class CorpusThread extends Thread {
	private final File file;
	public DetectionResults res;
	public final long creationTime;
	private final Vector<CorpusThreadException> errorLog;
	
	public CorpusThread(File file, Vector<CorpusThreadException> errorLog) {
		this.file=file;
		this.creationTime = System.currentTimeMillis();
		this.errorLog = errorLog;
		start();
	}
	
	public void run() {
		try {
			res = Main.cloneDetection(file.toPath(), Paths.get(file.getAbsolutePath()+"/src/main/java"));
		} catch(Exception e) {
			errorLog.add(new CorpusThreadException(file, e));
		}
	}

	public void timeout() {
		interrupt();
		errorLog.add(new CorpusThreadException(file, "Thread has exceeded its timeout!"));
	}
	
	public File getFile() {
		return file;
	}
}
