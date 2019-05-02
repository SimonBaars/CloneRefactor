package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Sequence implements Comparable<Sequence> {
	final List<Location> sequence;

	public Sequence(List<Location> collection) {
		super();
		this.sequence = collection;
	}

	public Sequence() {
		super();
		this.sequence = new ArrayList<>();
	}

	public Sequence(Collection<Location> values) {
		this(new ArrayList<>(values));
	}

	public List<Location> getSequence() {
		return sequence;
	}
	
	public Sequence add(Location l) {
		sequence.add(l);
		return this; //For method chaining
	}

	public int size() {
		return sequence.size();
	}

	@Override
	public String toString() {
		return "Sequence [sequence=" + Arrays.toString(sequence.toArray()) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sequence == null) ? 0 : sequence.hashCode());
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
		Sequence other = (Sequence) obj;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		return true;
	}

	public int getNodeSize() {
		return sequence.isEmpty() ? 0 : sequence.get(0).getAmountOfNodes();
	}
	
	public int getEffectiveLineSize() {
		return sequence.isEmpty() ? 0 : sequence.get(0).getEffectiveLines();
	}
	
	public int getTotalNodeVolume() {
		return sequence.stream().mapToInt(e -> e.getAmountOfNodes()).sum();
	}
	
	public int getTotalEffectiveLineVolume() {
		return sequence.stream().mapToInt(e -> e.getEffectiveLines()).sum();
	}

	public Location getAny() {
		return sequence.get(0);
	}

	@Override
	public int compareTo(Sequence o) {
		if(getNodeSize() == o.getNodeSize())
			return Integer.compare(o.size(), size());
		return Integer.compare(o.getNodeSize(), getNodeSize());
	}
}
