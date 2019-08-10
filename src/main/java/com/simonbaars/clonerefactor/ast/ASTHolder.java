package com.simonbaars.clonerefactor.ast;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class ASTHolder {
	private static Map<Long, Map<String, ClassOrInterfaceDeclaration>> classes = new ConcurrentHashMap<>();

	public static Map<String, ClassOrInterfaceDeclaration> getClasses(){
		return classes.get(Thread.currentThread().getId());
	}
	
	public static Map<String, ClassOrInterfaceDeclaration> setClasses(Map<String, ClassOrInterfaceDeclaration> c){
		return classes.put(Thread.currentThread().getId(), c);
	}
	
	public static Map<String, ClassOrInterfaceDeclaration> removeClasses(){
		return classes.remove(Thread.currentThread().getId());
	}
	
	public static Set<Long> activeThreads() {
		return classes.keySet();
	}
}
