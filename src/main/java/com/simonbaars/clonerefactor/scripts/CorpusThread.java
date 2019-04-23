package com.simonbaars.clonerefactor.scripts;

import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getJavaFiles;
import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getSourceFolder;

import java.io.File;

import com.simonbaars.clonerefactor.ast.CloneParser;
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
			res = new CloneParser().parse(getJavaFiles(getSourceFolder(file)));
		} catch (Exception e) {
			e.printStackTrace(); // For now we just want to debug this :D.
		}
	}
}
