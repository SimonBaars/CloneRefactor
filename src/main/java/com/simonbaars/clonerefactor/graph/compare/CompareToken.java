package com.simonbaars.clonerefactor.graph.compare;

import com.github.javaparser.JavaToken;

public class CompareToken extends Compare {
	
	private final JavaToken token;
	
	public CompareToken(JavaToken token) {
		super(token.getRange().get());
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && token.equals(((CompareToken)o).token);
	}

	@Override
	public int hashCode() {
		return token.hashCode();
	}

	@Override
	public String toString() {
		return "CompareToken [token=" + token.asString() + "]";
	}
}
