package com.simonbaars.clonerefactor.detection;

import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.compare.CloneType;
import com.simonbaars.clonerefactor.compare.CompareLiteral;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class Type2Variability {
	public static final int MAX_LITERAL_VARIABILITY_PERCENTAGE = 20;
	public static final int MAX_METHOD_CALL_VARIABILITY_PERCENTAGE = 20;
	
	public boolean determineVariability(Sequence s) {
		List<List<CompareLiteral>> literals = new ArrayList<>();
		for(Location l : s.getSequence()) {
			List<CompareLiteral> literals2 = l.getContents().getLiterals();
			literals.add(literals2);
			literals2.forEach(e -> e.setCloneType(CloneType.TYPE1));
		}
		int[][] equalityArray = new int[literals.size()][literals.get(0).size()];
		for(int i = 0; i<literals.size(); i++) {
			final List<CompareLiteral> differentCompareLiterals = new ArrayList<>();
			int curr = 0;
			for(int j = 0; j<literals.get(i).size(); j++) {
				int index = differentCompareLiterals.indexOf(literals.get(i).get(j));
				if(index == -1) {
					equalityArray[i][j] = curr++;
					differentCompareLiterals.add(literals.get(i).get(j));
				} else {
					equalityArray[i][j] = index;
				}
			}
		}
		int same;
		int diff;
		//for(int i = 0; i<equalityArray.length)
		return true;
	}
}
