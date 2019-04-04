package com.simonbaars.clonerefactor.datatype;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.model.LineTokens;

public class LineBuffer {
	int currentIndex = 0;
	int tokens = 0;
	private final List<LineTokens> lines;
	
	public LineBuffer() {
		lines = new ArrayList<>();
	}
	
	public void addToBuffer(List<Node> n, int minLines, int minTokens) {
		LineTokens prevLine = lines.get(currentIndex);
		if(prevLine!=null)
			tokens-=prevLine.size();
		lines.add(new LineTokens(n));
		tokens+=n.size();
		if(lines.size()>minLines)
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

	public List<LineTokens> getLines() {
		return lines;
	}
}
