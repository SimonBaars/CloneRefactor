package com.simonbaars.clonerefactor.detection.type2;

import java.util.stream.IntStream;

public class IndexRange {
	private final int start;
	private final int end;
	
	public IndexRange(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}
	
	public IndexRange(int singleton) {
		this.start = singleton;
		this.end = singleton;
	}

	public int getStart() {
		return start;
	}
	
	public int getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "IndexRange [start=" + start + ", end=" + end + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + end;
		result = prime * result + start;
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
		IndexRange other = (IndexRange) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}
	
	public IntStream stream() {
		return IntStream.range(start, end);
	}
	
	public int[] toArray(){
		return stream().toArray();
	}

	public IndexRange withStart(int start) {
		return new IndexRange(start, this.end);
	}
	
	public IndexRange withEnd(int end) {
		return new IndexRange(this.start, end);
	}
}
