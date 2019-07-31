package com.simonbaars.clonerefactor.ast.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;

public class CompareType extends Compare implements ResolvesSymbols {
	private final List<ClassOrInterfaceType> referenceType;
	private final List<ResolvedType> type;
	
	public CompareType(ReferenceType type) {
		super(type.getRange().get());
		this.referenceType = parseType(type);
		this.type = this.referenceType.stream().map(t -> resolve(t::resolve)).filter(t -> t.isPresent()).map(t -> t.get()).collect(Collectors.toList());
	}
	
	private List<ClassOrInterfaceType> parseType(ReferenceType type2) {
		List<ClassOrInterfaceType> types = new ArrayList<>();
		if(type2 instanceof ClassOrInterfaceType)
			types.add((ClassOrInterfaceType)type2);
		type2.accept(new VoidVisitorAdapter<List<ClassOrInterfaceType>>() {
			@Override
		    public void visit(final ClassOrInterfaceType n, final List<ClassOrInterfaceType> t) {
				super.visit(n, t);
				t.add(n);
			}
		}, types);
		return types;
	}

	public boolean equals(Object o) {
		if(!super.equals(o))
			return false;
		CompareType other = ((CompareType)o);
		if(referenceType.size() == type.size())
			return type.equals(other.type);
		return referenceType.equals(other.referenceType);
	}
	
	@Override
	public int hashCode() {
		if(referenceType.size() == type.size())
			return type.hashCode();
		return referenceType.hashCode();
	}

	@Override
	public String toString() {
		return "CompareType [type=" + type + "]";
	}
}
