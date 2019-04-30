package com.simonbaars.clonerefactor.compare;

public class CompareLiteral implements Compare {
	private final CloneType type;
	
	public CompareLiteral(CloneType type) {
		this.type = type;
	}
	
	@Override
	public boolean compare(Compare o, CloneType t) {
		return Compare.super.compare(o, t);
	}
	
	@Override
	public boolean equals(Object o) {
		return true; //We compare using the interface default compare method.
	}

	@Override
	public boolean isValid() {
		return type!=CloneType.TYPE1;
	}

	@Override
	public int getHashCode() {
		return -1;
	}
}
