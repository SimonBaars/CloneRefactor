package com.simonbaars.clonerefactor;

import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for the node locations.
 */
public class CloneRelationTest extends TestCase {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CloneRelationTest( String testName ) {
        super( testName );
    }
    
    @Override
    public void setUp() {
    	Settings.get().setCloneType(CloneType.TYPE1);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( CloneRelationTest.class );
    }
    
    public void testAncestor() {
        test("Ancestor", RelationType.ANCESTOR);
    }
    
    public void testFirstCousin() {
        test("FirstCousin", RelationType.FIRSTCOUSIN);
    }
    
    public void testSibling() {
        test("Sibling", RelationType.SIBLING);
    }
    
    public void testSuperClass() {
        test("SuperClass", RelationType.SUPERCLASS);
    }
    
    public void testMethod() {
        test("SingleFile", RelationType.SAMEMETHOD);
    }
    
    public void testNoRelation() {
        test("SimpleClone", RelationType.UNRELATED);
    }
    
    public void testSameClass() {
        test("SameClass", RelationType.SAMECLASS);
    }
    
    public void testSameExternalSuperClass() {
        test("ExternalSuperClass", RelationType.EXTERNALSUPERCLASS);
    }
    
    public void testSameHierarchy() {
        test("SameHierarchy", RelationType.COMMONHIERARCHY);
    }

	private void test(String name, RelationType loc) {
		DetectionResults r = testProject(name);
        Assert.assertEquals(loc, r.getMetrics().amountPerRelation.keySet().iterator().next());
	}

	private DetectionResults testProject(String project) {
		return Main.cloneDetection(CloneRelationTest.class.getClassLoader().getResource(project).getFile());
	}
}
