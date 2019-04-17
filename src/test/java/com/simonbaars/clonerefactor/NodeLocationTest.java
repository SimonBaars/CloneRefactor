package com.simonbaars.clonerefactor;

import com.simonbaars.clonerefactor.metrics.NodeLocation;
import com.simonbaars.clonerefactor.model.DetectionResults;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for the node locations.
 */
public class NodeLocationTest extends TestCase {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public NodeLocationTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( NodeLocationTest.class );
    }
    
    public void testAncestor() {
        test("Ancestor", NodeLocation.ANCESTOR);
    }
    
    public void testFirstCousin() {
        test("FirstCousin", NodeLocation.FIRSTCOUSIN);
    }
    
    public void testSibling() {
        test("Sibling", NodeLocation.SIBLING);
    }
    
    public void testSuperClass() {
        test("SuperClass", NodeLocation.SUPERCLASS);
    }
    
    public void testMethod() {
        test("SingleFile", NodeLocation.SAMEMETHOD);
    }
    
    public void testNoRelation() {
        test("SimpleClone", NodeLocation.UNRELATED);
    }
    
    public void testSameClass() {
        test("SameClass", NodeLocation.SAMECLASS);
    }
    
    public void testSameExternalSuperClass() {
        test("ExternalSuperClass", NodeLocation.EXTERNALSUPERCLASS);
    }
    
    public void testSameHierarchy() {
        test("SameHierarchy", NodeLocation.COMMONHIERARCHY);
    }

	private void test(String name, NodeLocation loc) {
		DetectionResults r = testProject(name);
        Assert.assertEquals(loc, r.getMetrics().amountPerLocation.keySet().iterator().next());
	}

	private DetectionResults testProject(String project) {
		return Main.cloneDetection(NodeLocationTest.class.getClassLoader().getResource(project).getFile());
	}
}
