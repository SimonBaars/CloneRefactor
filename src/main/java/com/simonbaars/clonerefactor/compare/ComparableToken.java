package com.simonbaars.clonerefactor.compare;

import com.github.javaparser.JavaToken;

public class ComparableToken implements Compare {
	
	private final JavaToken token;
	
	public ComparableToken(JavaToken token) {
		super();
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		return token.equals(((ComparableToken)o).token);
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
