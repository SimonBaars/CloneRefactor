package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.JavaToken;

public class CompareToken extends Compare {
	
	private final JavaToken token;
	
	public CompareToken(CloneType cloneType, JavaToken token) {
		super(cloneType);
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		return token.equals(((CompareToken)o).token);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public int getHashCode() {
		return token.hashCode();
	}

	@Override
	public String toString() {
		return "CompareToken [token=" + token.asString() + "]";
	}
}
