package com.simonbaars.clonerefactor.misc;

import com.simonbaars.clonerefactor.detection.type2.model.WeightedPercentage;
import com.simonbaars.clonerefactor.types.Type1Testcases;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class WeightedPercentageTest extends TestCase {
	 public static Test suite() {
	        return new TestSuite( Type1Testcases.class );
	    }
	    
	    public void testPercentageMerging() {
	    	assertEquals(100.0, new WeightedPercentage(100, 1).mergeWith(new WeightedPercentage(100, 1)).getPercentage());
	    }
	    
	    public void testMergeDifferentWeight() {
	    	assertEquals(100.0/3, new WeightedPercentage(100, 1).mergeWith(new WeightedPercentage(0, 2)).getPercentage());
	    }
}
