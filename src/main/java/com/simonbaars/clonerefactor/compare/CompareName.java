package com.simonbaars.clonerefactor.compare;

public class CompareName extends Compare {
	public CompareName(CloneType type) {
		super(type);
	}

	@Override
	public boolean equals(Object o) {
		return true; //Type two names will always be flagged as equals, as we don't take them into account.
	}

	@Override
	public boolean isValid() {
		return cloneType.isNotTypeOne(); //We make it invalid if the type is 1, so it will be compared using token comparison.
	}

	@Override
	public int getHashCode() {
		return -2;
	}
}
