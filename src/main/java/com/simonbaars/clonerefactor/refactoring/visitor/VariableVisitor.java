package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.ast.resolution.ResolveVariable;
import com.simonbaars.clonerefactor.ast.resolution.ResolvedVariable;

public class VariableVisitor extends VoidVisitorAdapter<Map<SimpleName, ResolvedType>> implements ResolvesSymbols {

	private Map<String, ClassOrInterfaceDeclaration> classes;

	public VariableVisitor(Map<String, ClassOrInterfaceDeclaration> classes) {
		this.classes = classes;
	}

	@Override
	public void visit(SimpleName ne, Map<SimpleName, ResolvedType> arg) {
		super.visit(ne, arg);
		System.out.println(ne);
		Optional<ResolvedVariable> resolvedVar = new ResolveVariable(classes, ne).findDeclaration();
		if(resolvedVar.isPresent()) {
			Optional<ResolvedType> type = resolvedVar.get().resolveType();
			System.out.println(resolvedVar.get().toString()+ ", "+type.isPresent());
			if(type.isPresent()) {
				arg.put(ne, type.get());
			}
			
		} else {
			System.out.println(ne+" not found");
		}
		//Optional<ResolvedValueDeclaration> mr = resolve(ne::resolve);
		//if(mr.isPresent()) {
		//	try { // GOES WRONG WAY TOO OFTEN! TODO TODO TODO	
		//		arg.put(ne, mr.get().getType());
		//	} catch (Exception e) {
				//e.printStackTrace(); TODO TODO
		//	}
		//}
	}
}