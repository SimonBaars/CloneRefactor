package com.simonbaars.clonerefactor.ast.interfaces;

public interface SetsIfNotNull {
	public default <T> T setIfNotNull(T oldObject, T newObject) {
		return newObject == null ? oldObject : newObject;
	}
}
