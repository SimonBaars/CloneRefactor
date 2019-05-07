package com.simonbaars.clonerefactor.thread;

import java.io.File;

public class CorpusThreadException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3571339641261493607L;
	File file;

	public CorpusThreadException(File file, String message, Throwable cause) {
		super(message, cause);
		this.file = file;
	}

	public CorpusThreadException(File file, String message) {
		super(message);
		this.file = file;
	}

	public CorpusThreadException(File file, Throwable cause) {
		super(cause);
		this.file = file;
	}

}
