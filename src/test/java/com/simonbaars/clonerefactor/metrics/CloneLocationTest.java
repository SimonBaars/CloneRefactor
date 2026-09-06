package com.simonbaars.clonerefactor.metrics;

import org.junit.Assert;
import org.junit.Test;

import com.simonbaars.clonerefactor.context.enums.LocationType;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.helper.Type1Test;

/**
 * Unit test for the node locations.
 */
public class CloneLocationTest extends Type1Test {
    
    @Test
    public void testClassLevel() {
        test("SeveralMethodsCloned", LocationType.CLASSLEVEL);
    }
    
    @Test
    public void testEnumLevel() {
        test("EnumClone", LocationType.ENUMLEVEL);
    }
    
    @Test
    public void testInterfaceLevel() {
        test("InterfaceClone", LocationType.INTERFACELEVEL);
    }
    
    @Test
    public void testMethodLevel1() {
        test("SingleFile", LocationType.METHODLEVEL);
    }
    
    @Test
    public void testMethodLevel2() {
        test("SimpleClone", LocationType.METHODLEVEL);
    }

	private void test(String name, LocationType loc) {
		DetectionResults r = testProject(name);
        Assert.assertEquals(loc, r.getMetrics().amountPerLocation.keySet().iterator().next());
	}
}
