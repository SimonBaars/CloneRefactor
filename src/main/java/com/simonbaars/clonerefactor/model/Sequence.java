package com.simonbaars.clonerefactor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;

import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.model.location.Location;

public class Sequence implements Comparable<Sequence> {
	private final List<Location> locations;
	
	private RelationType relationType;
	private Refactorability refactorability;

	public Sequence(List<Location> collection) {
		super();
		this.locations = collection;
	}

	public Sequence() {
		super();
		this.locations = new ArrayList<>();
	}

	public Sequence(Sequence copy, int begin, int end) {
		this.locations = copy.locations.subList(begin, end);
	}

	public List<Location> getLocations() {
		return locations;
	}
	
	public Sequence add(Location l) {
		locations.add(l);
		return this; //For method chaining
	}

	public int size() {
		return locations.size();
	}

	@Override
	public String toString() {
		return "Sequence [sequence=" + Arrays.toString(locations.toArray()) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((locations == null) ? 0 : locations.hashCode());
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
		return locations == null ? other.locations == null : locations.equals(other.locations);
	}

	public int getNodeSize() {
		return locations.isEmpty() ? 0 : locations.get(0).getAmountOfNodes();
	}
	
	public int getEffectiveLineSize() {
		return locations.isEmpty() ? 0 : locations.get(0).getEffectiveLines();
	}
	
	public int getTotalNodeVolume() {
		return getTotalVolume(Location::getAmountOfNodes);
	}
	
	public int getTotalTokenVolume() {
		return getTotalVolume(Location::getAmountOfTokens);
	}
	
	public int getTotalEffectiveLineVolume() {
		return getTotalVolume(Location::getEffectiveLines);
	}
	
	public int getTotalVolume(ToIntFunction<? super Location> mapper) {
		return locations.stream().mapToInt(mapper).sum();
	}

	public Location getAny() {
		return locations.get(0);
	}

	@Override
	public int compareTo(Sequence o) {
		if(getTotalNodeVolume() == o.getTotalNodeVolume())
			return Integer.compare(o.getTotalTokenVolume(), getTotalTokenVolume());
		return Integer.compare(o.getTotalNodeVolume(), getTotalNodeVolume());
	}
	
	public void setMetrics(CloneRelation relation, CloneRefactorability r) {
		relationType = relation.get(this);
		refactorability = r.get(this);
	}
	
	public RelationType getRelationType() {
		return relationType;
	}
	
	public Refactorability getRefactorability() {
		return refactorability;
	}
	
	public Sequence isValid() {
		if(locations.size()<2)
			throw new IllegalStateException("Not enough locations for "+this);
		//if(locations.stream().map(e -> e.getContents().getNodes().size()).distinct().count()>1)
		//	throw new IllegalStateException("Unequal location node sizes for "+this);
		//if(locations.stream().map(e -> e.getContents().getCompare().size()).distinct().count()>1)
		//	throw new IllegalStateException("Unequal location compare sizes for "+this);
		return this;
	}
}
