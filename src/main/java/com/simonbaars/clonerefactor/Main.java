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

public class Main {

	public static void main(String[] args) {
		if(args.length == 0)
			System.err.println("Please enter the path of the project you want to scan for clones!");
		
		List<File> javaFiles = scanProjectForJavaFiles(args);
		
		if(javaFiles.size() == 0)
			System.err.println("Project does not contain any clones!");
		
		ASTParser.parse(javaFiles);
	}

	private static List<File> scanProjectForJavaFiles(String[] args) {
		List<File> javaFiles = new ArrayList<>();
		try {
			Files.walkFileTree(Paths.get(args[0]), new SimpleFileVisitor<Path>() {
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
