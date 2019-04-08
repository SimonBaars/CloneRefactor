package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;

public class CompilationUnitReg {
	private int lastLineNumber = 0;
	private List<Node> thisLine = new ArrayList<>();
	private Location lastLine = null;
	
	public CompilationUnitReg() {}
	
	public int getLastLineNumber() {
		return lastLineNumber;
	}

	public void setLastLineNumber(int lastLineNumber) {
		this.lastLineNumber = lastLineNumber;
	}
	
	public boolean lastLineNumberExists() {
		return lastLineNumber>0;
	}

	public void visitLine(int line) {
		this.lastLineNumber = line;
	}

	public List<Node> getThisLine() {
		return thisLine;
	}
	
	public void nextLine(Location lineLoc) {
		lineLoc.setPrevLine(lastLine);
		if(getLastLine()!=null) getLastLine().setNextLine(lineLoc);
		this.setLastLine(lineLoc);
		thisLine = new ArrayList<>();
	}

	public Location getLastLine() {
		return lastLine;
	}

	public void setLastLine(Location lastLine) {
		this.lastLine = lastLine;
	}

	public void reset() {
		setLastLineNumber(0);
		thisLine.clear();
	}

}
