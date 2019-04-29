package com.simonbaars.clonerefactor.compare;

public interface Compare {
	public default boolean compare(Compare c, int type) {
		if(this.getClass() != c.getClass())
			return false;
		else return this.equals(c);
	}
}
