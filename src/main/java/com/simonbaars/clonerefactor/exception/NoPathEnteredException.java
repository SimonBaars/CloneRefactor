package com.simonbaars.clonerefactor.exception;

public class NoPathEnteredException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2972109211543532640L;

	public NoPathEnteredException() {
		super("No path was entered!");
	}

	public NoPathEnteredException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoPathEnteredException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoPathEnteredException(String message) {
		super(message);
	}

	public NoPathEnteredException(Throwable cause) {
		super(cause);
	}

	
}
