package com.simonbaars.clonerefactor.thread.results;

import com.simonbaars.clonerefactor.thread.CorpusThread;

public interface WritesResults {
	public void writeResults(CorpusThread t);
	public void finalize();
}
