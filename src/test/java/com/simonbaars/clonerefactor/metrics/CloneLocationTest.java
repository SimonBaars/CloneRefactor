package com.simonbaars.clonerefactor.metrics;

import com.simonbaars.clonerefactor.context.context.enums.LocationType;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.helper.Type1Test;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for the node locations.
 */
public class CloneLocationTest extends Type1Test {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CloneLocationTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( CloneLocationTest.class );
    }
    
    public void testClassLevel() {
        test("SeveralMethodsCloned", LocationType.CLASSLEVEL);
    }
    
    public void testEnumLevel() {
        test("EnumClone", LocationType.ENUMLEVEL);
    }
    
    public void testInterfaceLevel() {
        test("InterfaceClone", LocationType.INTERFACELEVEL);
    }
    
    public void testMethodLevel1() {
        test("SingleFile", LocationType.METHODLEVEL);
    }
    
    public void testMethodLevel2() {
        test("SimpleClone", LocationType.METHODLEVEL);
    }

	private void test(String name, LocationType loc) {
		DetectionResults r = testProject(name);
        Assert.assertEquals(loc, r.getMetrics().amountPerLocation.keySet().iterator().next());
	}
}
