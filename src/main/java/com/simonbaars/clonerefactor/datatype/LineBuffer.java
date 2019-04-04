package com.simonbaars.clonerefactor.datatype;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.javaparser.ast.Node;

public class LineBuffer {
	int currentIndex = 0;
	int tokens = 0;
	private final List<List<Node>> lines;
	
	public LineBuffer() {
		lines = new ArrayList<>();
	}
	
	public void addToBuffer(List<Node> n) {
		List<Node> prevLine = lines.get(currentIndex);
		if(prevLine!=null)
			tokens-=prevLine.size();
		lines.set(currentIndex, n);
		currentIndex++;
		tokens+=n.size();
		if(currentIndex>=lines.size())
			currentIndex=0;
	}
	
	public boolean isValid() {
		return lines.stream().noneMatch(Objects::isNull);
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public List<List<Node>> getLines() {
		return lines;
	}
}
