package com.simonbaars.clonerefactor.ast.compare;

import java.util.Collections;
import java.util.List;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ReferenceType;
import com.simonbaars.clonerefactor.ast.interfaces.HasCompareList;
import com.simonbaars.clonerefactor.model.location.HasRange;
import com.simonbaars.clonerefactor.settings.CloneType;

public abstract class Compare implements HasRange {
	private CloneType cloneType;
	private Range range;
	private Node belongsToStatement;
	
	protected Compare(Range range) {
		this.range = range;
	}
	
	protected Compare(CloneType cloneType, Range range) {
		this(range);
		this.cloneType=cloneType;
	}
	
	public static Compare create(Node statement, Node node, JavaToken e, CloneType cloneType) {
		Compare compare = null;
		if(node!=null) {
			if(node instanceof ReferenceType)
				compare = new CompareType((ReferenceType)node);
			else if(node instanceof NameExpr)
				compare = new CompareVariable((NameExpr)node);
			else if(node instanceof LiteralExpr)
				compare = new CompareLiteral((LiteralExpr)node, cloneType);
			else if(node instanceof SimpleName)
				compare = new CompareName((SimpleName)node);
			else if(node instanceof MethodCallExpr)
				compare = new CompareMethodCall((MethodCallExpr)node);
		}
		if(compare == null)
			compare = new CompareToken(e);
		compare.setCloneType(cloneType);
		compare.belongsToStatement = statement;
		return compare;
	}
	
	/**
	 * These nodes are compared by node rather than token.
	 * @return
	 */
	public static boolean comparingNode(Node node) {
		return node instanceof ReferenceType || node instanceof NameExpr || node instanceof LiteralExpr || node instanceof SimpleName || node instanceof MethodCallExpr;
	}

	@Override
	public boolean equals(Object o) {
		return this.getClass() == o.getClass();
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
	public void setCloneType(CloneType type) {
		this.cloneType = type;
	}
	
	public List<Compare> relevantChildren(Node statement, HasCompareList locationContents){
		return Collections.emptyList();
	}

	public Range getRange() {
		return range;
	}
	
	public boolean doesType2Compare() {
		return false;
	}
	
	protected CloneType getCloneType() {
		return cloneType;
	}

	public Node getBelongsToStatement() {
		return belongsToStatement;
	}
	
	public Expression getExpression(){
		return null;
	}
}
