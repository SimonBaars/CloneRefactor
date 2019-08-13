package clonegraph.compare;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SimpleName;

public class CompareName extends Compare {
	private final SimpleName name;
	
	public CompareName(SimpleName name) {
		super(name.getRange().get());
		this.name=name;
	}

	@Override
	public boolean equals(Object o) {
		if(!super.equals(o))
			return false;
		CompareName other = (CompareName)o;
		if(getCloneType().isNotType1() && nameNotCompared(name.getParentNode().get()))
			return true;
		return name.equals(other.name);
	}

	private boolean nameNotCompared(Node node) {
		return node instanceof MethodDeclaration || node instanceof ClassOrInterfaceDeclaration || node instanceof EnumDeclaration;
	}

	@Override
	public int hashCode() {
		return getCloneType().isNotType1() ? -2 : name.hashCode();
	}

	@Override
	public String toString() {
		return "CompareName [t=" + name + "]";
	}
}
