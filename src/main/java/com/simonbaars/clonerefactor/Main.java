package com.simonbaars.clonerefactor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
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
		/*System.out.println("COLLECT SYMBOLS");
		Log.setAdapter(new StandardOutStandardErrorAdapter());
		ProjectRoot r = new SymbolSolverCollectionStrategy().collect(path); 
		System.out.println("DONE COLLECT");
		Optional<SourceRoot> sr = r.getSourceRoot(sourceRoot);
		System.out.println("DONE SR");
		if(!sr.isPresent())
			return null; //Faulty project
			*/
		CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(sourceRoot));
        StaticJavaParser.getConfiguration().setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));
        SourceRoot root = new SourceRoot(sourceRoot);
		return new CloneParser().parse(root);
	}

}
