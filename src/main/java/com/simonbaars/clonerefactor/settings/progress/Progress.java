package com.simonbaars.clonerefactor.settings.progress;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;

public class Progress implements CalculatesPercentages{
	private static final int MAX_PRINTS_PER_STAGE = 100;
	private Stage currentStage;
	private int processed = 0;
	private int total = 0;
	
	public Progress(Path sourceRoot) {
		total = scanProjectForJavaFiles(sourceRoot).size();
		reset();
	}
	
	private List<File> scanProjectForJavaFiles(Path sourceRoot) {
		List<File> javaFiles = new ArrayList<>();
		try {
			Files.walkFileTree(sourceRoot, new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
			    	File file = filePath.toFile();
					if(file.getName().endsWith(".java"))
			    		javaFiles.add(file);
			    	return FileVisitResult.CONTINUE;
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return javaFiles;
	}
	
	public void nextStage(int total) {
		nextStage();
		this.total = total;
	}
	
	public void next() {
		processed++;
		if(processed % ((total / MAX_PRINTS_PER_STAGE)+1) == 0)
			System.out.println(toString());
	}
	
	@Override
	public String toString() {
		return currentStage+" ("+processed+" / "+total+")";
	}

	public void nextStage() {
		currentStage = Stage.values()[currentStage.ordinal()+1];
		this.processed = 0;
	}

	public void reset() {
		this.currentStage = Stage.BUILDAST;
	}
}
