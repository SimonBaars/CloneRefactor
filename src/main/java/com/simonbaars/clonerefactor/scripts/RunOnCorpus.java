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

	public static void main(String[] args) {
		File outputFolder = new File("/Users/sbaars/clone/output");
		outputFolder.mkdirs();
		File[] corpusFiles = getFilteredCorpusFiles(5, 1000);
		Metrics fullMetrics = new Metrics();
		for(File file : ProgressBar.wrap(Arrays.asList(corpusFiles), new ProgressBarBuilder().setTaskName("Running Clone Detection").setStyle(ProgressBarStyle.ASCII))) {
			DetectionResults res = new CloneParser().parse(getJavaFiles(getSourceFolder(file)));
			fullMetrics.add(res.getMetrics());
			try {
				FileUtils.writeStringToFile(new File(outputFolder.getAbsolutePath()+"/"+file.getName()+"-"+res.getClones().size()+".txt"), res.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(fullMetrics);
	}
}
