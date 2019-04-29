package com.simonbaars.clonerefactor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.utils.SourceRoot;
import com.simonbaars.clonerefactor.ast.CloneParser;
import com.simonbaars.clonerefactor.exception.NoJavaFilesFoundException;
import com.simonbaars.clonerefactor.model.DetectionResults;

public class Main {

	public static void main(String[] args) {
		System.out.println("Start parse at "+DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(LocalDateTime.now()));
		if(args.length == 0)
			new NoJavaFilesFoundException();
		
		System.out.println(cloneDetection(args[0]));
	}

	public static DetectionResults cloneDetection(String path) {
		return cloneDetection(Paths.get(path));
	}

	public static DetectionResults cloneDetection(Path path) {
		ParserConfiguration config = new ParserConfiguration()
				.setLexicalPreservationEnabled(false) //Disabled for now, we'll enable it when we start refactoring.
				.setStoreTokens(true);
		SourceRoot sourceRoot = new SourceRoot(path, config);
		return new CloneParser().parse(sourceRoot);
	}

}
