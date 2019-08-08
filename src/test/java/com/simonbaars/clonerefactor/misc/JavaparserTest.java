package com.simonbaars.clonerefactor.misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.utils.SourceRoot;
import com.simonbaars.clonerefactor.metrics.CloneContentsTest;
import com.simonbaars.clonerefactor.settings.CloneType;

import junit.framework.TestCase;

public class JavaparserTest extends TestCase {
	public void testJavaParserBug() {
    	System.out.println("testSingleFile");
    	Path p = Paths.get(CloneContentsTest.class.getClassLoader().getResource(CloneType.TYPE1R.getNicelyFormatted()).getFile()+File.separator+"SingleFile");
    	SourceRoot s = new SourceRoot(p);
    	ParseResult<CompilationUnit> pcu;
		try {
			pcu = s.tryToParse().get(0);
			CompilationUnit cu = pcu.getResult().get();
			ClassOrInterfaceDeclaration classDec = cu.getClassByName("Clone1").get();
			MethodDeclaration method = classDec.getMethods().get(0);
			BlockStmt body = method.getBody().get();
			List<Statement> statements = body.getStatements();
			List<Statement> clones = statements.stream().filter(e -> e.toString().contains("I'm a clone")).collect(Collectors.toList());
			IntStream.range(10,clones.size()).forEach(i -> clones.get(i).getParentNode().get().remove(clones.get(i)));
			IntStream.range(0, clones.size()).forEach(i -> System.out.println(clones.get(i).getParentNode()));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }
}
