package com.simonbaars.clonerefactor.scripts;

import java.io.File;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.model.DetectionResults;

public class CorpusThread extends Thread {
	private final File file;
	public DetectionResults res;
	
	public CorpusThread(File file) {
		this.file=file;
		start();
	}
	
	public void run() {
		try {
			res = Main.cloneDetection(file.toPath());
		} catch (Exception e) {
			e.printStackTrace(); // For now we just want to debug this :D.
		}
	}
}
