package com.simonbaars.clonerefactor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
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
		return cloneDetection(path, path);
	}
	
	public static DetectionResults cloneDetection(Path path, Path sourceRoot) {
		CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(sourceRoot));
       
        final ParserConfiguration config = new ParserConfiguration()
    			.setLexicalPreservationEnabled(false) //Disabled for now, we'll enable it when we start refactoring.
    			.setStoreTokens(true)
    			.setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));
        SourceRoot root = new SourceRoot(sourceRoot);
		return new CloneParser().parse(root, config);
	}
	
	public static DetectionResults parseProject(Path path, Path sourceRoot) {
		CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(sourceRoot));
        
        File file = new File(path.toString()+File.separator+"lib");
		if(file.exists()) {
        	for(File f : file.listFiles(new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".jar");
				}
			})) {
        		try {
					combinedTypeSolver.add(new JarTypeSolver(f));
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        }
        
		final ProjectRoot projectRoot = 
			    new SymbolSolverCollectionStrategy()
			    .collect(path);
		projectRoot.addSourceRoot(sourceRoot);
		final ParserConfiguration config = new ParserConfiguration()
    			.setLexicalPreservationEnabled(false) //Disabled for now, we'll enable it when we start refactoring.
    			.setStoreTokens(true)
    			.setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));
		
		return new CloneParser().parse(projectRoot.getSourceRoot(sourceRoot).get(), config);
	}

}
