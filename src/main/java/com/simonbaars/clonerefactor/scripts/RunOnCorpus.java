package com.simonbaars.clonerefactor.scripts;

import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getFilteredCorpusFiles;
import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getJavaFiles;
import static com.simonbaars.clonerefactor.scripts.PrepareProjectsFolder.getSourceFolder;

import java.io.File;
import java.util.Arrays;

import com.simonbaars.clonerefactor.ast.ASTParser;

import me.tongfei.progressbar.ProgressBar;

public class RunOnCorpus {

	public static void main(String[] args) {
		File[] corpusFiles = getFilteredCorpusFiles();
		for(File file : ProgressBar.wrap(Arrays.asList(corpusFiles), "Running Clone Detection")) {
			ASTParser.parse(getJavaFiles(getSourceFolder(file)));
		}
	}

}
