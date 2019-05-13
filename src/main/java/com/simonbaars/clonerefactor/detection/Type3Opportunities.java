package com.simonbaars.clonerefactor.detection;

import java.util.List;

import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class Type3Opportunities {
	private ListMap<Integer, FileLocations> opportunities = new ListMap<>();
	
	
	public void determineType3Opportunities(List<Sequence> clones) {
		for(Sequence seq : clones) {
			FileLocations fl = new FileLocations(seq.getSequence());
			if(opportunities.containsKey(fl.hashCode())) {
				 List<FileLocations> otherFls = opportunities.get(fl.hashCode());
				 for(FileLocations loc : otherFls) {
					 if(isType3(fl, loc) || isType3(loc, fl)) {
						 
					 }
				 }
			} 
			opportunities.addTo(fl.hashCode(), fl);
		}
	}
	
	public boolean isType3(FileLocations fl1, FileLocations fl2) {
		Location lastFL1 = fl1.getLocs().get(fl1.getLocs().size()-1);
		Location firstFL1 = fl2.getLocs().get(0);
		return false;
	}
}
