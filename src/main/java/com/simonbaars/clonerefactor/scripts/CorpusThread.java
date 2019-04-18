package com.simonbaars.clonerefactor.scripts;

import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getJavaFiles;
import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getSourceFolder;

import java.io.File;
import java.io.IOException;

import com.simonbaars.clonerefactor.ast.CloneParser;
import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.util.FileUtils;

public class CorpusThread extends Thread {
	private final File file;
	private final File outputFolder;
	private static final Metrics fullMetrics = new Metrics();
	
	public CorpusThread(File file, File outputFolder) {
		this.file=file;
		this.outputFolder = outputFolder;
		start();
	}
	
	public void run() {
		DetectionResults res = new CloneParser().parse(getJavaFiles(getSourceFolder(file)));
		addToFullMetrics(res);
		try {
			FileUtils.writeStringToFile(new File(outputFolder.getAbsolutePath()+"/"+file.getName()+"-"+res.getClones().size()+".txt"), res.toString());
			writeFullMetricsState();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void addToFullMetrics(DetectionResults res) {
		fullMetrics.add(res.getMetrics());
	}
	
	private synchronized void writeFullMetricsState() throws IOException {
		FileUtils.writeStringToFile(new File(outputFolder.getAbsolutePath()+"/full_metrics.txt"), fullMetrics.toString());
	}
}
