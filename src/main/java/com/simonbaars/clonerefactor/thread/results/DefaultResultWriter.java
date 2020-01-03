package com.simonbaars.clonerefactor.thread.results;

import java.io.File;
import java.io.IOException;

import com.simonbaars.clonerefactor.core.util.SavePaths;
import com.simonbaars.clonerefactor.datatype.map.SimpleTable;
import com.simonbaars.clonerefactor.metrics.Metrics;
import com.simonbaars.clonerefactor.thread.CalculatesTimeIntervals;
import com.simonbaars.clonerefactor.thread.CorpusThread;
import com.simonbaars.clonerefactor.thread.WritesErrors;

public class DefaultResultWriter implements WritesResults, WritesErrors, CalculatesTimeIntervals {

	private final File OUTPUT_FOLDER = new File(SavePaths.getFullOutputFolder());
	private final File FULL_METRICS = new File(OUTPUT_FOLDER.getParent()+"/metrics.txt");
	public final Metrics fullMetrics = new Metrics();
	private final SimpleTable refactorResults = new SimpleTable("System", "Nodes", "Tokens", "Relation", "Returns", "Arguments", "Duplication", "Complexity", "Interface Size", "Size", "Duplication (nodes)", "Size (nodes)");
	
	public DefaultResultWriter() {
		OUTPUT_FOLDER.mkdirs();
	}

	@Override
	public void writeResults(CorpusThread t) {
		calculateGeneralMetrics(t);
		fullMetrics.add(t.res.getMetrics());
		refactorResults.addAll(t.res.getRefactorResults());
		if(t.res.getMetrics().getChild().isPresent()) {
			if(fullMetrics.getChild().isPresent())
				fullMetrics.getChild().get().add(t.res.getMetrics().getChild().get());
			else fullMetrics.setChild(t.res.getMetrics().getChild().get());
		}
		try {
			writeStringToFile(new File(OUTPUT_FOLDER.getAbsolutePath()+File.separator+t.getFile().getName()+"-"+t.res.getClones().size()+".txt"), t.res.toString());
			writeStringToFile(FULL_METRICS, fullMetrics.toString());
		} catch (IOException e) {
			writeProjectError(t.getFile().getName(), e);
		}
	}
	
	private void calculateGeneralMetrics(CorpusThread t) {
		int duration = interval(t.creationTime);
		t.res.getMetrics().generalStats.increment("Duration", duration);
		t.res.getMetrics().averages.addTo("Average duration", duration);
	}
	
	@Override
	public void finalize() {
		try {
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"refactor.txt"), refactorResults.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
