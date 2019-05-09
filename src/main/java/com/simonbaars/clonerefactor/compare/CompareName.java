package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.JavaToken;

public class CompareName extends Compare {
	JavaToken t;
	
	public CompareName(CloneType type, JavaToken t) {
		super(type);
		this.t = t;  
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && (cloneType.isNotTypeOne() || t.equals(((CompareName)o).t)); //Type two names will always be flagged as equals, as we don't take them into account.
	}

	@Override
	public int getHashCode() {
		return -2;
	}
}
