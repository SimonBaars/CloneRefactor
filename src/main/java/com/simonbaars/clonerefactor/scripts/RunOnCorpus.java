package com.simonbaars.clonerefactor.scripts;

import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getFilteredCorpusFiles;
import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getJavaFiles;
import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getSourceFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.simonbaars.clonerefactor.ast.CloneParser;
import com.simonbaars.clonerefactor.common.TestingCommons;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.util.PrettyPrinter;

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;

public class RunOnCorpus {

	public static void main(String[] args) {
		File outputFolder = new File("/Users/sbaars/clone/output");
		outputFolder.mkdirs();
		File[] corpusFiles = getFilteredCorpusFiles(5, 1000);
		for(File file : ProgressBar.wrap(Arrays.asList(corpusFiles), new ProgressBarBuilder().setTaskName("Running Clone Detection").setStyle(ProgressBarStyle.ASCII))) {
			List<Sequence> seq = new CloneParser().parse(getJavaFiles(getSourceFolder(file)));
			try {
				TestingCommons.writeStringToFile(new File(outputFolder.getAbsolutePath()+"/"+file.getName()+"-"+seq.size()+".txt"), PrettyPrinter.prettify(seq));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
