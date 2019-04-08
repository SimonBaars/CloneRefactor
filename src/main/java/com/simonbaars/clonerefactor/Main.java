package com.simonbaars.clonerefactor;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.ast.ASTParser;
import com.simonbaars.clonerefactor.exception.NoJavaFilesFoundException;
import com.simonbaars.clonerefactor.exception.NoPathEnteredException;
import com.simonbaars.clonerefactor.model.Sequence;

public class Main {

	public static void main(String[] args) {
		if(args.length == 0)
			new NoJavaFilesFoundException();
		
		cloneDetection(args[0]);
	}

	public static List<Sequence> cloneDetection(String path) {
		List<File> javaFiles = scanProjectForJavaFiles(path);
		
		if(javaFiles.size() == 0)
			throw new NoPathEnteredException();
		
		return ASTParser.parse(javaFiles);
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
