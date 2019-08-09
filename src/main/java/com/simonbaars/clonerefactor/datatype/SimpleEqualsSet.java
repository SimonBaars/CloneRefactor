package com.simonbaars.clonerefactor.datatype;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleEqualsSet<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8612281192407855831L;
	
	public SimpleEqualsSet() {
	}
	
	public SimpleEqualsSet(Collection<? extends E> es) {
		es.forEach(this::add);
	}
	
	@Override
	public boolean add(E e) {
		if(stream().anyMatch(i -> i == e))
			return false;
		return super.add(e);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> es) {
		es.forEach(this::add);
		return true;
	}
	
	@Override
	public boolean remove(Object e) {
		for(int i = 0; i<size(); i++) {
			if(get(i) == e) {
				remove(i);
				return true;
			}
		}
		return false;
	}
}
