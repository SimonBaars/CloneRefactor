package com.simonbaars.clonerefactor.ast.compare;

import com.github.javaparser.JavaToken;

public class CompareLiteral extends Compare {
	final JavaToken t;
	
	public CompareLiteral(JavaToken t) {
		super(t.getRange().get());
		this.t=t;
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o) && (getCloneType().isNotTypeOne() || t.equals(((CompareLiteral)o).t)); 
	}

	@Override
	public int hashCode() {
		return getCloneType().isNotTypeOne() ? -1 : t.hashCode();
	}

	@Override
	public String toString() {
		return "CompareLiteral [t=" + t + "]";
	}
	
	@Override
	public boolean doesType2Compare() {
		return true;
	}
}
