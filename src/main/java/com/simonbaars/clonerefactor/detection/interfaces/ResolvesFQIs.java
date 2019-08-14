package com.simonbaars.clonerefactor.detection.interfaces;

import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.simonbaars.clonerefactor.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.graph.ASTHolder;

public interface ResolvesFQIs extends RequiresNodeContext {
	public default String getFullyQualifiedName(ClassOrInterfaceDeclaration childClass, ClassOrInterfaceType t) {
		String name = "";
		if(t.getScope().isPresent())
			name+=getFullyQualifiedName(childClass, t.getScope().get())+".";
		else if(childClass!=null) {
			Optional<CompilationUnit> compilationUnit = getCompilationUnit(childClass);
			if(!compilationUnit.isPresent())
				return t.asString();
			Optional<String> nameOpt = compilationUnit.get().getImports().stream().map(e -> e.getNameAsString()).filter(e -> e.endsWith("."+t.getName())).findAny();
			if(nameOpt.isPresent()) 
				return nameOpt.get();
			else {
				String fullyQualifiedName = getFullyQualifiedName(childClass);
				name+=fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf('.')+1);
				if(!ASTHolder.getClasses().containsKey(name+t.getNameAsString()) && compilationUnit.get().getImports().stream().anyMatch(e -> e.isAsterisk())) {
					Optional<String> asteriks = compilationUnit.get().getImports().stream().filter(e -> e.isAsterisk()).map(e -> e.getNameAsString()+"."+t.getNameAsString()).filter(e -> ASTHolder.getClasses().containsKey(e)).findAny();
					if(asteriks.isPresent())
						return asteriks.get();
				}
			}
		}
		return name+t.getNameAsString();
	}
	
	public default String getFullyQualifiedName(ClassOrInterfaceDeclaration c2) {
		String name = "";
		if(c2 == null)
			return null;
		Optional<ClassOrInterfaceDeclaration> parentClass = c2.getParentNode().isPresent() ? getClass(c2.getParentNode().get()) : Optional.empty();
		if(parentClass.isPresent()) {
			name+=getFullyQualifiedName(parentClass.get())+".";
		} else {
			Optional<CompilationUnit> u = getCompilationUnit(c2);
			if(u.isPresent() && u.get().getPackageDeclaration().isPresent()) {
				name+=u.get().getPackageDeclaration().get().getNameAsString()+".";
			}
		}
		return name+c2.getNameAsString();
	}
}
