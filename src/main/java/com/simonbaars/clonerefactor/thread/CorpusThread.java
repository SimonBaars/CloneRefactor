package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.util.concurrent.TimeoutException;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.settings.Settings;

public class CorpusThread extends Thread {
	private final File file;
	private final File sourceRoot;
	public DetectionResults res;
	public final long creationTime;
	public Exception error;
	public final Settings settings;
	
	public CorpusThread(Settings settings, File file, File sourceRoot) {
		this.settings = settings;
		this.file=file;
		this.creationTime = System.currentTimeMillis();
		this.sourceRoot = sourceRoot;
		start();
	}
	
	public void run() {
		try {
			res = Main.cloneDetection(settings, file.toPath(), sourceRoot.toPath());
			res.sorted();
		} catch(Exception e) {
			error = e;
		}
	}

	@SuppressWarnings("deprecation")
	public void timeout() {
		stop();
		error = new TimeoutException("Thread has exceeded its timeout!");
	}
	
	public File getFile() {
		return file;
	}
}
