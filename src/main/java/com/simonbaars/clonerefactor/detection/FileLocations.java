package com.simonbaars.clonerefactor.detection;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.model.Location;

public class FileLocations {
	private final List<Location> locs;
	
	public FileLocations (List<Location> locs) {
		Collections.sort(locs);
		this.locs = locs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
        for (Location e : locs) 
        	result = prime*result + (e==null ? 0 : e.getFile().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileLocations other = (FileLocations) obj;
		if (locs == null || other.locs == null)
			return false;
		return locs.size() == other.locs.size() && IntStream.range(0,locs.size()).allMatch(i -> locs.get(i).getFile().equals(other.locs.get(i).getFile()));
	}
	
	
	
}
