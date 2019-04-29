package com.simonbaars.clonerefactor.compare;

public class ComparableLiteral implements Compare {
	private final CloneType type;
	
	public ComparableLiteral(CloneType type) {
		this.type = type;
	}
	
	@Override
	public boolean compare(Compare o, CloneType t) {
		return Compare.super.compare(o, t);
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
