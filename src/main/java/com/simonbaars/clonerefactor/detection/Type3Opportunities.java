package com.simonbaars.clonerefactor.detection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simonbaars.clonerefactor.model.Sequence;

public class Type3Opportunities {
	private Map<FileLocations, Sequence> opportunities = new HashMap<>();
	
	
	public void determineType3Opportunities(List<Sequence> clones) {
		for(Sequence seq : clones) {
			FileLocations fl = new FileLocations(seq.getSequence());
			if(opportunities.containsKey(fl)) {
				
			}
		}
	}
}
