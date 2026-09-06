package com.simonbaars.clonerefactor.misc;

import org.junit.Assert;
import org.junit.Test;

import com.simonbaars.clonerefactor.detection.type2.model.WeightedPercentage;

public class WeightedPercentageTest {
	    
	    @Test
	    public void testPercentageMerging() {
	    	Assert.assertEquals(100.0, new WeightedPercentage(100, 1).mergeWith(new WeightedPercentage(100, 1)).getPercentage(), 0.001);
	    }
	    
	    @Test
	    public void testMergeDifferentWeight() {
	    	Assert.assertEquals(100.0/3, new WeightedPercentage(100, 1).mergeWith(new WeightedPercentage(0, 2)).getPercentage(), 0.001);
	    }
}
