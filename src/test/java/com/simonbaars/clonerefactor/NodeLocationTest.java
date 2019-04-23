package com.simonbaars.clonerefactor;

import com.simonbaars.clonerefactor.metrics.enums.CloneRelation;
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
        test("Ancestor", CloneRelation.ANCESTOR);
    }
    
    public void testFirstCousin() {
        test("FirstCousin", CloneRelation.FIRSTCOUSIN);
    }
    
    public void testSibling() {
        test("Sibling", CloneRelation.SIBLING);
    }
    
    public void testSuperClass() {
        test("SuperClass", CloneRelation.SUPERCLASS);
    }
    
    public void testMethod() {
        test("SingleFile", CloneRelation.SAMEMETHOD);
    }
    
    public void testNoRelation() {
        test("SimpleClone", CloneRelation.UNRELATED);
    }
    
    public void testSameClass() {
        test("SameClass", CloneRelation.SAMECLASS);
    }
    
    public void testSameExternalSuperClass() {
        test("ExternalSuperClass", CloneRelation.EXTERNALSUPERCLASS);
    }
    
    public void testSameHierarchy() {
        test("SameHierarchy", CloneRelation.COMMONHIERARCHY);
    }

	private void test(String name, CloneRelation loc) {
		DetectionResults r = testProject(name);
        Assert.assertEquals(loc, r.getMetrics().amountPerLocation.keySet().iterator().next());
	}

	private DetectionResults testProject(String project) {
		return Main.cloneDetection(NodeLocationTest.class.getClassLoader().getResource(project).getFile());
	}
}
