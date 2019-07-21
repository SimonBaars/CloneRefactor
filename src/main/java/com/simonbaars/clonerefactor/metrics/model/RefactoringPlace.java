package com.simonbaars.clonerefactor.metrics.model;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.datatype.map.ListMap;
import com.simonbaars.clonerefactor.metrics.context.enums.RelationType;

public class RefactoringPlace implements Comparable<RefactoringPlace>{
	private final ListMap<ComparingClasses, ClassOrInterfaceDeclaration> place = new ListMap<>();
	private RelationType relation;
	
	
	public RefactoringPlace(RelationType relation) {
		super();
		this.relation = relation;
	}
	
	public RefactoringPlace(ComparingClasses cc, ClassOrInterfaceDeclaration...decls) {
		super();
		this.place.addTo(cc, decls);
	}
	
	public RelationType getRelation() {
		return relation;
	}
	public void setRelation(RelationType relation) {
		this.relation = relation;
	}
	public ListMap<ComparingClasses, ClassOrInterfaceDeclaration> getPlace() {
		return place;
	}
	
	public void merge(RefactoringPlace p) {
		if(compareTo(p)>0) {
			place.clear();
			relation = p.relation;
		}
		if(p.relation == relation)
			place.putAll(p.place);
	}

	@Override
	public int compareTo(RefactoringPlace o) {
		return Integer.compare(relation.ordinal(), o.relation.ordinal());
	}
	
	
}
