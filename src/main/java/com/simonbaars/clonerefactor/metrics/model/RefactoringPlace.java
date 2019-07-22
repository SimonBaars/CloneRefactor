package com.simonbaars.clonerefactor.metrics.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.datatype.map.ListMap;
import com.simonbaars.clonerefactor.metrics.context.enums.RelationType;

public class RefactoringPlace implements Comparable<RefactoringPlace>{
	private final List<ClassOrInterfaceDeclaration> place = new ArrayList<>();
	private RelationType relation;
	
	
	public RefactoringPlace(RelationType relation) {
		super();
		this.relation = relation;
	}
	
	public RefactoringPlace(ClassOrInterfaceDeclaration...decls) {
		super();
		Collections.addAll(this.place, decls);
	}
	
	public RelationType getRelation() {
		return relation;
	}
	public void setRelation(RelationType relation) {
		this.relation = relation;
	}
	public List<ClassOrInterfaceDeclaration> getPlace() {
		return place;
	}
	
	public void merge(RefactoringPlace p) {
		if(compareTo(p)>0) {
			place.clear();
			relation = p.relation;
		}
		if(p.relation == relation)
			place.addAll(p.place);
	}

	@Override
	public int compareTo(RefactoringPlace o) {
		return Integer.compare(relation.ordinal(), o.relation.ordinal());
	}
	
	
}
