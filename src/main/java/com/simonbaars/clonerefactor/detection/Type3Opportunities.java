package com.simonbaars.clonerefactor.detection;

import java.util.List;

import com.simonbaars.clonerefactor.datatype.ListMap;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;
import com.simonbaars.clonerefactor.model.location.Type3Location;

public class Type3Opportunities {
	private ListMap<Integer, FileLocations> opportunities = new ListMap<>();
	
	
	public void determineType3Opportunities(List<Sequence> clones) {
		for(Sequence seq : clones) {
			FileLocations fl = new FileLocations(seq);
			if(opportunities.containsKey(fl.hashCode())) {
				 List<FileLocations> otherFls = opportunities.get(fl.hashCode());
				 for(FileLocations loc : otherFls) {
					 if(isType3(fl, loc) || isType3(loc, fl)) {
						 clones.remove(fl.getSeq());
						 clones.remove(loc.getSeq());
						 clones.add(merge(fl.getLocs(), loc.getLocs()));
					 }
				 }
			} 
			opportunities.addTo(fl.hashCode(), fl);
		}
	}
	
	private Sequence merge(List<Location> fl1, List<Location> fl2) {
		Sequence seq = new Sequence();
		for(int i = 0; i<fl1.size(); i++) {
			seq.add(new Type3Location(fl1.get(i), fl2.get(i)));
		}
		return seq;
	}

	//TODO
	public boolean isType3(FileLocations fl1, FileLocations fl2) {
		
		return false;
	}
}
