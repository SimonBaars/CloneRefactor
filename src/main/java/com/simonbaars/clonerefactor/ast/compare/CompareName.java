package com.simonbaars.clonerefactor.ast.compare;

import com.github.javaparser.JavaToken;

public class CompareName extends Compare {
	JavaToken t;
	
	public CompareName(JavaToken t) {
		super(t.getRange().get());
		this.t = t;  
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && (getCloneType().isNotTypeOne() || t.equals(((CompareName)o).t)); //Type two names will always be flagged as equals, as we don't take them into account.
	}

	@Override
	public int hashCode() {
		return getCloneType().isNotTypeOne() ? -2 : t.hashCode();
	}

	@Override
	public String toString() {
		return "CompareName [t=" + t + "]";
	}
}
