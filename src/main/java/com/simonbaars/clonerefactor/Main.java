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
import java.util.List;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.SymbolResolver;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.utils.SourceRoot.Callback.Result;
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
