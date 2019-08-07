package com.simonbaars.clonerefactor.refactoring.visitor;

import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedValueDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.ast.interfaces.ResolvesSymbols;
import com.simonbaars.clonerefactor.refactoring.visitor.ResolvedVariable.VariableType;

public class VariableVisitor extends VoidVisitorAdapter<Map<NameExpr, ResolvedType>> implements ResolvesSymbols {

	@Override
	public void visit(NameExpr ne, Map<NameExpr, ResolvedType> arg) {
		super.visit(ne, arg);
		Optional<ResolvedValueDeclaration> mr = resolve(ne::resolve);
		if(mr.isPresent()) {
			try { // GOES WRONG WAY TOO OFTEN! TODO TODO TODO	
				arg.put(ne, mr.get().getType());
			} catch (Exception e) {
				//e.printStackTrace(); TODO TODO
			}
		}
	}
}