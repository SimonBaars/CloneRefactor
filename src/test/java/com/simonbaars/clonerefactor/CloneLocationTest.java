package com.simonbaars.clonerefactor;

import com.simonbaars.clonerefactor.metrics.enums.CloneLocation.LocationType;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.model.DetectionResults;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for the node locations.
 */
public class CloneLocationTest extends TestCase {

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

	private DetectionResults testProject(String project) {
		return Main.cloneDetection(CloneLocationTest.class.getClassLoader().getResource(project).getFile());
	}
}
