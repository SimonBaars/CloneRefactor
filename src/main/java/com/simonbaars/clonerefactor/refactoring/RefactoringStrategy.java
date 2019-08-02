package com.simonbaars.clonerefactor.refactoring;

public enum RefactoringStrategy {
	DONOTREFACTOR, //Does not do any refactoring
	SIMULATE, //Does not save any refactorings to disk, only collects their metrics
	REPLACE, //Replaces original code with refactored code.
	COPYONLYCHANGED,  //Copies only the files that were changed to a different location, not changing the original.
	COPYALL, //Copies the entire project to a different location and applies the refactorings there.
	GITREPLACE, //Replaces the refactorings in the original project, but creates a git commit after each change
	GITCOPY //Copies the entire project to a new location and creates a git commit after each change
;

	public boolean originalLocation() {
		return this == REPLACE || this == GITREPLACE;
	}

	public boolean copyAll() {
		return this == COPYALL || this == GITCOPY;
	}
	
	public boolean usesGit() {
		return this == GITREPLACE || this == GITCOPY;
	}
	
	public boolean savesFiles() {
		return this != SIMULATE;
	}

	public RefactoringStrategy revertToNonGit() {
		if(this == GITREPLACE)
			return REPLACE;
		if(this == GITCOPY)
			return COPYALL;
		throw new IllegalStateException("Not using Git, so cannot revert!");
	}
}
