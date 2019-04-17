package com.simonbaars.clonerefactor.metrics;

import java.io.File;
import java.util.List;

import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class MetricCollector {
	private ListMap<File, Integer> parsedLines = new ListMap<>();
	private ListMap<File, Range> parsedRanges = new ListMap<>();
	private Metrics metrics = new Metrics();
	
	public MetricCollector() {}
	
	public void reportFoundNode(Location l) {
		metrics.totalAmountOfLines+=getUnparsedLines(l);
		metrics.totalAmountOfNodes+=l.getAmountOfNodes();
		metrics.totalAmountOfTokens+=l.getAmountOfTokens();
	}
	
	private int getUnparsedLines(Location l) {
		int amountOfLines = 0;
		for(int i = l.getRange().begin.line; i<=l.getRange().end.line; i++) {
			List<Integer> lines = parsedLines.get(l.getFile());
			if(!lines.contains(i)) {
				amountOfLines++;
				lines.add(i);
			} 
		}
		return amountOfLines;
	}
	
	public void reportClones(List<Sequence> clones) {
		parsedLines.clear();
		for(Sequence clone : clones)
			reportClone(clone);
	}

	private void reportClone(Sequence clone) {
		metrics.amountPerCloneClassSize.increment(clone.size());
		for(Location l : clone.getSequence()) {
			reportClonedLocation(l);
		}
	}

	private void reportClonedLocation(Location l) {
		metrics.amountOfLinesCloned+=getUnparsedLines(l);
		metrics.amountOfTokensCloned+=l.getAmountOfTokens(); //TODO: This is incorrect, as clones may be checked several times.
		metrics.amountOfNodesCloned+=l.getAmountOfNodes(); //TODO: Also incorrect, same reason.
	}
}
