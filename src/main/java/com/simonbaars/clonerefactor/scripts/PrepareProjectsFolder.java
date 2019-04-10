package com.simonbaars.clonerefactor.scripts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrepareProjectsFolder {

	public static void main(String[] args) {
		File projectsFolder = new File("/Users/sbaars/Downloads/java_projects/");
		System.out.println(projectsFolder.list().length);
		System.out.println(projectsFolder.listFiles(f -> isQualified(getSourceFolder(f))).length);
	}
	
	public static File[] getFilteredCorpusFiles() {
		File projectsFolder = new File("/Users/sbaars/Downloads/java_projects/");
		return projectsFolder.listFiles(f -> isQualified(getSourceFolder(f)));
	}

	public static File getSourceFolder(File f) {
		return new File(f.getAbsolutePath()+"/src/main/java");
	}
	
	public static boolean isQualified(File project) {
		return project.exists() && between(10, 100, countJavaFiles(project));
	}

	private static boolean between(int i, int j, long l) {
		return l >=i && l <= j;
	}

	private static long countJavaFiles(File project) {
			return getJavaFileStream(project).count();
	}

	private static Stream<Path> getJavaFileStream(File project) {
		try {
			return Files.walk(project.toPath())
		        .parallel()
		        .filter(p -> !p.toFile().isDirectory() && p.toString().endsWith(".java"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static List<File> getJavaFiles(File project){
		return getJavaFileStream(project).map(e -> e.toFile()).collect(Collectors.toList());
	}
	
}
