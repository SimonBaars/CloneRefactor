package com.simonbaars.clonerefactor.clonegraph.resolution;

import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.resolution.types.ResolvedType;
import com.simonbaars.clonerefactor.clonegraph.interfaces.ResolvesSymbols;

public class ResolvedVariable implements ResolvesSymbols {
	public enum VariableType{
		CLASSFIELD, SUPERCLASSFIELD, LOCAL, METHODPARAMETER;
	}
	
	private final Type type;
	private final SimpleName name;
	private final Node node;
	private final VariableType variableType;
	
	public ResolvedVariable(Type type, SimpleName name, Node node, VariableType variableType) {
		super();
		this.type = type;
		this.name = name;
		this.node = node;
		this.variableType = variableType;
	}
	
	public Type getType() {
		return type;
	}
	
	public SimpleName getName() {
		return name;
	}
	
	public Node getNode() {
		return node;
	}
	
	public VariableType getVariableType() {
		return variableType;
	}

	@Override
	public String toString() {
		return "ResolvedVariable [type=" + type + ", name=" + name + ", node=" + node + ", variableType=" + variableType
				+ "]";
	}
	
	public Optional<ResolvedType> resolveType() {
		return resolve(type::resolve);
	}
}
