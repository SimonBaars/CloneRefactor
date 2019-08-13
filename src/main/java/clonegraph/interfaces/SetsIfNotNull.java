package clonegraph.interfaces;

import java.util.Optional;

public interface SetsIfNotNull {
	public default <T> T setIfNotNull(T oldObject, T newObject) {
		return newObject == null ? oldObject : newObject;
	}
	
	public default <T> T setIfNotEmpty(T oldObject, Optional<T> newObject) {
		return newObject.isPresent() ? newObject.get() : oldObject;
	}
}
