package com.simonbaars.clonerefactor.detection.type3;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;

public class FileLocations {
	private final Sequence seq;
	
	public FileLocations (Sequence seq) {
		Collections.sort(seq.getLocations());
		this.seq = seq;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
        for (Location e : seq.getLocations()) 
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
		return seq.getLocations().size() == seq.getLocations().size() && IntStream.range(0, seq.getLocations().size()).allMatch(i -> seq.getLocations().get(i).getFile().equals(other.seq.getLocations().get(i).getFile()));
	}

	public List<Location> getLocs() {
		return seq.getLocations();
	}

	public Sequence getSeq() {
		return seq;
	}

	@Override
	public String toString() {
		return String.format("FileLocations [seq=%s]", seq);
	}
	
	
}
