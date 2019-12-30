package com.simonbaars.clonerefactor.detection.type3;

import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.detection.model.location.LocationContents;

public interface Type3Calculation {
	public default LocationContents calculateDiffContents(Location before, Location after) {
		LocationContents contents = new LocationContents(before.getContents().settings);
		Location line = before;
		while((line = line.getNext()) != null) {
			if(line.getRange().isBefore(after.getRange().begin)) {
				if(line.getRange().isAfter(before.getRange().end)){
					populateContents(contents, line.getContents());
				}
			} else break;
		}
		return contents;
	}

	public default void populateContents(LocationContents contents, LocationContents otherContents) {
		contents.getNodes().addAll(otherContents.getNodes());
		contents.getTokens().addAll(otherContents.getTokens());
		contents.getCompare().addAll(otherContents.getCompare());
	}
	
	public default int calculateDiff(Location before, Location after) {
		return calculateDiff(calculateDiffContents(before, after));
	}
	
	public default int calculateDiff(LocationContents diff) {
		return diff.getNodes().size();
	}
}
