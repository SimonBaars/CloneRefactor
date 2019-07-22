package com.simonbaars.clonerefactor.refactoring;

public enum RefactoringStrategy {
	REPLACE, //Replaces original code with refactored code.
	COPYONLYCHANGED,  //Copies only the files that were changed to a different location, not changing the original.
	COPYALL, //Copies the entire project to a different location and applies the refactorings there.
	GITREPLACE, //Replaces the refactorings in the original project, but creates a git commit after each change
	GITCOPY //Copies the entire project to a new location and creates a git commit after each change
}
