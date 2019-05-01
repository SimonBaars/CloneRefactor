package com.simonbaars.clonerefactor.compare;

public class CompareLiteral extends Compare {
	public CompareLiteral(CloneType cloneType) {
		super(cloneType);
	}
	
	@Override
	public boolean equals(Object o) {
		return true; //Type two literals will always be flagged as equals, as we don't take them into account.
	}

	@Override
	public boolean isValid() {
		return cloneType.isNotTypeOne(); //We make it invalid if the type is 1, so it will be compared using token comparison.
	}

	@Override
	public int getHashCode() {
		return -1;
	}
}
