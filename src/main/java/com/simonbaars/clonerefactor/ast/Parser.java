package com.simonbaars.clonerefactor.ast;

public interface Parser {
	public default <T> T setIfNotNull(T l, T parseClassFile) {
		return parseClassFile == null ? l : parseClassFile;
	}
}
