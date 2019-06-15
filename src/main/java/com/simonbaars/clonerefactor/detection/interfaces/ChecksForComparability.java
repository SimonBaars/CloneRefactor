package com.simonbaars.clonerefactor.detection.interfaces;

import java.util.stream.IntStream;

import com.simonbaars.clonerefactor.detection.type2.model.Type2Contents;
import com.simonbaars.clonerefactor.detection.type2.model.Type2Location;

public interface ChecksForComparability {
	public default boolean isComparable(Type2Location location1, Type2Location location2) {
		return isComparable(location1.contentArray(), location2.contentArray());
	}
	
	public default boolean isComparable(Type2Contents location1Contents, Type2Contents location2Contents) {
		return isComparable(location1Contents.getContents(), location2Contents.getContents());
	}
	
	public default boolean isComparable(int[] location1Contents, int[] location2Contents) {
		return location1Contents.length==location2Contents.length && IntStream.range(0, location1Contents.length).filter(k -> location1Contents[k] < 0 || location2Contents[k] < 0).noneMatch(k -> location1Contents[k]!=location2Contents[k]);
	}
}
