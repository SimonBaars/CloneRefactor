package com.simonbaars.clonerefactor.exception;

import com.github.javaparser.Range;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;

public class NoTokensException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7065943898151219929L;

	public NoTokensException() {
		super();
	}

	public NoTokensException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoTokensException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoTokensException(String message) {
		super(message);
	}

	public NoTokensException(Throwable cause) {
		super(cause);
	}
	
	public NoTokensException(Node n, TokenRange tokens, Range validRange) {
		super("Node of type "+n.getClass()+" = "+n+System.lineSeparator() +
				"tokens = "+tokens+System.lineSeparator() +
				"validRange = "+validRange);
	}

}
