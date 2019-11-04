package com.simonbaars.clonerefactor.scripts.intimals.similarity;

import java.util.List;
import java.util.stream.Stream;

public interface HasImportance<T extends HasImportance<T>> {
	public boolean isMoreImportant(T other);
	
	public default T getMostImportant(List<T> stuff) {
		return stuff.stream().reduce((e1, e2) -> e1.isMoreImportant(e2) ? e1 : e2).get();
	}
	
	public default T getMostImportant(Stream<T> stuff) {
		return stuff.reduce((e1, e2) -> e1.isMoreImportant(e2) ? e1 : e2).get();
	}
}
