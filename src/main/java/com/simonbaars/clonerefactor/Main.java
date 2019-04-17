package com.simonbaars.clonerefactor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.utils.Pair;
import com.simonbaars.clonerefactor.ast.CloneParser;
import com.simonbaars.clonerefactor.exception.NoJavaFilesFoundException;
import com.simonbaars.clonerefactor.exception.NoPathEnteredException;
import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.model.Sequence;

public class Main {

	public static void main(String[] args) {
		System.out.println("Start parse at "+DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(LocalDateTime.now()));
		if(args.length == 0)
			new NoJavaFilesFoundException();
		
		List<Sequence> cloneDetection = cloneDetection(args[0]);
		System.out.println(Arrays.toString(cloneDetection.toArray()));
		System.out.println("Done ("+cloneDetection.size()+" clones found) at "+DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(LocalDateTime.now()));
	}

	public static Pair<List<Sequence>, Metrics> cloneDetection(String path) {
		List<File> javaFiles = Arrays.asList(new File(path));
		if(javaFiles.get(0).isDirectory())
		    javaFiles = scanProjectForJavaFiles(path);
		
		if(javaFiles.size() == 0)
			throw new NoPathEnteredException();
		
		CloneParser cloneParser = new CloneParser();
		return new Pair<>(cloneParser.parse(javaFiles), cloneParser.metricCollector.getMetrics());
	}

	private static List<File> scanProjectForJavaFiles(String path) {
		List<File> javaFiles = new ArrayList<>();
		try {
			Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
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

}
