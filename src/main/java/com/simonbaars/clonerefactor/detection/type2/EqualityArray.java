package com.simonbaars.clonerefactor.detection.type2;

import java.util.Arrays;

public class EqualityArray {
	private final int[] array;

	public EqualityArray(int[] array) {
		super();
		this.array = array;
	}

	public int[] getArray() {
		return array;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(array);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EqualityArray other = (EqualityArray) obj;
		if (!Arrays.equals(array, other.array))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EqualityArray [array=" + Arrays.toString(array) + "]";
	}
}
