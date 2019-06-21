package com.simonbaars.clonerefactor.datatype;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.simonbaars.clonerefactor.detection.type2.model.WeightedPercentage;

public class AverageMap<K> extends HashMap<String, WeightedPercentage> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AverageMap() {
	}

	public AverageMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public AverageMap(int initialCapacity) {
		super(initialCapacity);
	}

	public AverageMap(Map<String, WeightedPercentage> m) {
		super(m);
	}
	
	public WeightedPercentage addTo(String key, double d) {
        return addTo(key, d, 1);
    }
	
	public WeightedPercentage addTo(String key, double d, int weight) {
        return addTo(key, new WeightedPercentage(d, weight));
    }
	
	public WeightedPercentage addTo(String key, WeightedPercentage p) {
		if(super.containsKey(key)) {
			WeightedPercentage wp = super.get(key);
			wp.mergeWith(p);
			return wp;
		}
        return super.put(key, p);
	}
	
	@Override 
	public WeightedPercentage get(Object key){
		if(!super.containsKey(key))
			super.put((String)key, new WeightedPercentage(0, 0));
		return super.get(key);
	}
	
	public double getPerc(Object key){
		if(!super.containsKey(key))
			return 0;
		return super.get(key).getPercentage();
	}
	
	@Override
	public String toString() {
		return keySet().stream().sorted().map(e -> e + "\t" + get(e)).collect(Collectors.joining(System.lineSeparator()));
	}

	public void addAll(AverageMap<K> amountPerCloneClassSize) {
		amountPerCloneClassSize.entrySet().stream().forEach(e -> this.addTo(e.getKey(), e.getValue()));
	}
}
