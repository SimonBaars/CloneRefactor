package com.simonbaars.clonerefactor.detection;

import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.compare.CompareLiteral;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

public class Type2Variability {
	public static final int MAX_LITERAL_VARIABILITY_PERCENTAGE = 20;
	public static final int MAX_METHOD_CALL_VARIABILITY_PERCENTAGE = 20;
	
	public boolean determineVariability(Sequence s) {
		List<List<CompareLiteral>> literals = new ArrayList<>();
		for(Location l : s.getSequence()) {
			literals.add(l.getContents().getLiterals());
		}
		int equality = 0;
		for(int i = 0; i<literals.get(0).size(); i++) {
			
		}
		return true;
	}
}
