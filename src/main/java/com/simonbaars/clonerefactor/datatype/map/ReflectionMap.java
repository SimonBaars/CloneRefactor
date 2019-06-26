package com.simonbaars.clonerefactor.datatype.map;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ReflectionMap<K, V> extends HashMap<K, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -40647186202640015L;

	public ReflectionMap() {
		super();
	}

	public ReflectionMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public ReflectionMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ReflectionMap(Map<? extends K, ? extends V> m) {
		super(m);
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object o) {
		if(!containsKey(o)) {
			try {
				put((K)o, (V)o.getClass().getConstructor().newInstance());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		return super.get(o);
	}
}
