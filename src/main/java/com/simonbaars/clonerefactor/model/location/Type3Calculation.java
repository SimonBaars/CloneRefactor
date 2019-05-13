package com.simonbaars.clonerefactor.model.location;

public interface Type3Calculation {
	public default LocationContents calculateDiffContents(Location before, Location after) {
		LocationContents contents = new LocationContents();
		Location line;
		while((line = before.getNextLine()) != null) {
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
}
