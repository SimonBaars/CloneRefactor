package com.simonbaars.clonerefactor.compare;

public enum CloneType {
	TYPE1,TYPE2,TYPE3;

	public boolean isNotTypeOne() {
		return this!=TYPE1;
	}
}
