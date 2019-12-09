package com.simonbaars.clonerefactor.scripts.intimals.model;

import com.github.javaparser.Position;
import com.github.javaparser.Range;

public class SimpleRange {
	private final int beginLine; 
	private final int endLine;
	
	public SimpleRange(int beginLine, int endLine) {
		super();
		this.beginLine = beginLine;
		this.endLine = endLine;
	}

	public SimpleRange(Position start, Position end) {
		super();
		this.beginLine = start.line;
		this.endLine = end.line;
	}
	
	public SimpleRange(Range range) {
		this(range.begin, range.end);
	}
	
	public int getBeginLine() {
		return beginLine;
	}

	public int getEndLine() {
		return endLine;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + beginLine;
		result = prime * result + endLine;
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
		SimpleRange other = (SimpleRange) obj;
		if (beginLine != other.beginLine)
			return false;
		if (endLine != other.endLine)
			return false;
		return true;
	}

	public boolean contains(SimpleRange patternRange) {
		return beginLine >= patternRange.beginLine && endLine <= patternRange.endLine;
	}
}
