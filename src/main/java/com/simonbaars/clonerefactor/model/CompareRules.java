package com.simonbaars.clonerefactor.model;

public class CompareRules {
	private boolean checkClassNames = false;
	private boolean checkParameterNames = false;
	private boolean checkMethodNames = false;
	private int maxAmountOfDifferentExpressions = 4;
	
	public CompareRules(boolean checkClassNames, boolean checkParameterNames, boolean checkMethodNames,
			int maxAmountOfDifferentExpressions) {
		super();
		this.checkClassNames = checkClassNames;
		this.checkParameterNames = checkParameterNames;
		this.checkMethodNames = checkMethodNames;
		this.maxAmountOfDifferentExpressions = maxAmountOfDifferentExpressions;
	}
	
	public CompareRules() {
		super();
	}

	public boolean isCheckClassNames() {
		return checkClassNames;
	}
	
	public void setCheckClassNames(boolean checkClassNames) {
		this.checkClassNames = checkClassNames;
	}
	
	public boolean isCheckParameterNames() {
		return checkParameterNames;
	}
	
	public void setCheckParameterNames(boolean checkParameterNames) {
		this.checkParameterNames = checkParameterNames;
	}
	
	public boolean isCheckMethodNames() {
		return checkMethodNames;
	}
	
	public void setCheckMethodNames(boolean checkMethodNames) {
		this.checkMethodNames = checkMethodNames;
	}
	
	public int getMaxAmountOfDifferentExpressions() {
		return maxAmountOfDifferentExpressions;
	}
	
	public void setMaxAmountOfDifferentExpressions(int maxAmountOfDifferentExpressions) {
		this.maxAmountOfDifferentExpressions = maxAmountOfDifferentExpressions;
	}
}
