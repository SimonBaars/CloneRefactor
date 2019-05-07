package com.simonbaars.clonerefactor.datatype;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

public class IndexedVector<E> extends Vector<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9150447915872304886L;
	
	public synchronized int getIndex(Class<E> c) {
		int size = size();
		try {
			E el = c.getConstructor(int.class).newInstance(size);
			add(el);
			if(indexOf(el)!=size)
				throw new IllegalStateException("Synchronization error!");
			return size;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException(e);
		}
	}
	
}
