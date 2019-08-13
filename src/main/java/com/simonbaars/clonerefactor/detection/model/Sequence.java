package com.simonbaars.clonerefactor.detection.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;

import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneRefactorability;
import com.simonbaars.clonerefactor.metrics.context.analyze.CloneRelation;
import com.simonbaars.clonerefactor.metrics.context.enums.Refactorability;
import com.simonbaars.clonerefactor.metrics.context.enums.RelationType;
import com.simonbaars.clonerefactor.metrics.model.Relation;

public class Sequence implements Comparable<Sequence> {
	private final List<Location> locations;
	
	private Relation relation;
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
		return this;
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
		return locations.isEmpty() ? 0 : getAny().getAmountOfNodes();
	}
	
	public int getEffectiveLineSize() {
		return locations.isEmpty() ? 0 : getAny().getAmountOfLines();
	}
	
	public int getTotalNodeVolume() {
		return getTotalVolume(Location::getAmountOfNodes);
	}
	
	public int getTotalTokenVolume() {
		return getTotalVolume(Location::getAmountOfTokens);
	}
	
	public int getTotalLineVolume() {
		return getTotalVolume(Location::getAmountOfLines);
	}
	
	public int getTotalVolume(ToIntFunction<? super Location> mapper) {
		return locations.stream().mapToInt(mapper).sum();
	}

	public Location getAny() {
		return locations.get(0);
	}

	@Override
	public int compareTo(Sequence o) {
		if(getNodeSize() == o.getNodeSize())
			return Integer.compare(o.getTotalTokenVolume(), getTotalTokenVolume());
		return Integer.compare(o.getNodeSize(), getNodeSize());
	}
	
	public void setMetrics(CloneRelation relation, CloneRefactorability r) {
		setRelation(relation);
		this.refactorability = r.get(this);
	}
	
	public void setRelation(CloneRelation relation) {
		this.relation = relation.get(this);
	}
	
	public RelationType getRelationType() {
		return this.relation.getType();
	}
	
	public Refactorability getRefactorability() {
		return this.refactorability;
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

	public boolean overlapsWith(Sequence s) {
		for(int i = 0; i<locations.size(); i++) {
			for(int j = 0; j<s.getLocations().size(); j++) {
				if(locations.get(i).getFile().equals(s.getLocations().get(j).getFile()) && locations.get(i).overlapsWith(s.getLocations().get(j))) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void sortLocations(boolean reverse) {
		Collections.sort(locations);
		if(reverse) Collections.reverse(locations);
	}

	public Relation getRelation() {
		return relation;
	}

	public int getTokenSize() {
		return getAny().getAmountOfTokens();
	}
}
