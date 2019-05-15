package com.simonbaars.clonerefactor.detection;

import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.detection.type2.WeightedPercentage;

public interface CalculatesPercentages {
	public default double calcPercentage(int part, int whole) {
		return (double)part/(double)whole*100D;
	}
	
	public default double diffPerc(int[] arr1, int[] arr2) {
		int same = 0, diff = 0;
		for(int i = 0; i<arr1.length; i++){
			if(arr1[i] == arr2[i]) same++; 
			else diff++;
		}
		return calcPercentage(diff, same+diff);
	}
	
	public default double diffPerc(int[][] arr) {
		int same = 0, diff = 0;
		for(int i = 0; i<arr.length; i++) {
			for(int j = i+1; j<arr.length; j++) {
				for(int k = 0; k<arr[i].length; k++){
					if(arr[i][k] == arr[j][k]) same++; 
					else diff++;
				}
			}
		}
		return calcPercentage(diff, same+diff);
	}
	
	public default double calcAvg(List<WeightedPercentage> percentages) {
		percentages = new ArrayList<>(percentages);
		while(percentages.size()>1) {
			percentages.set(0, percentages.get(0).mergeWith(percentages.get(1)));
			percentages.remove(1);
		}
		return percentages.get(0).getPercentage();
	}
}
