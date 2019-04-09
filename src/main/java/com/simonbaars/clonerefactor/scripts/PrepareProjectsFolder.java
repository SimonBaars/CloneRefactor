package com.simonbaars.clonerefactor.scripts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PrepareProjectsFolder {

	public static void main(String[] args) {
		File projectsFolder = new File("/Users/sbaars/Downloads/java_projects/");
		System.out.println(projectsFolder.list().length);
		System.out.println(projectsFolder.listFiles(f -> isQualified(new File(f.getAbsolutePath()+"/src/main/java"))).length);
	}
	
	public static boolean isQualified(File project) {
		return project.exists() && between(10, 100, countJavaFiles(project));

	private static boolean between(int i, int j, long countJavaFiles) {
		return countJavaFiles >=i && countJavaFiles <= j;
	}

	private static long countJavaFiles(File project) {
		try {
			return Files.walk(project.toPath())
			        .parallel()
			        .filter(p -> !p.toFile().isDirectory() && p.toString().endsWith(".java"))
			        .count();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
