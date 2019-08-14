package com.simonbaars.clonerefactor.context.relation;

import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.simonbaars.clonerefactor.graph.interfaces.ResolvesSymbols;

public interface ResolvesFullyQualifiedIdentifiers extends ResolvesSymbols {
	public default String getFullyQualifiedName(ClassOrInterfaceType t) {
		Optional<ResolvedReferenceType> type = resolve(t::resolve);
		if(type.isPresent())
			return type.get().getQualifiedName();
		return t.getNameAsString();
	}

	public default String getFullyQualifiedName(ClassOrInterfaceDeclaration t) {
		Optional<ResolvedReferenceTypeDeclaration> type = resolve(t::resolve);
		if(type.isPresent())
			return type.get().getQualifiedName();
		return t.getNameAsString();
	}
}
