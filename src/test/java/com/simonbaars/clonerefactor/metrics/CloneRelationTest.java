package com.simonbaars.clonerefactor.metrics;

import com.simonbaars.clonerefactor.context.context.enums.RelationType;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.helper.Type1Test;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for the node locations.
 */
public class CloneRelationTest extends Type1Test {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CloneRelationTest( String testName ) {
        super( testName );
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
    
    public void testExternalAncestor() {
        test("ExternalAncestor", RelationType.EXTERNALANCESTOR);
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
    
    public void testSameInterface() {
        test("SameInterface", RelationType.SAMEINTERFACE);
    }
    
    public void testSameInterfaceInSuperclass() {
        test("SameInterfaceInSuperclass", RelationType.SAMEINTERFACE);
    }

    public void testSameInterfaceInInterfaceHierarchy() {
        test("SameInterfaceInInterfaceHierarchy", RelationType.SAMEINTERFACE);
    }
    
    public void testNoDirectSuperclass() {
        test("SimpleClone", RelationType.NODIRECTSUPERCLASS);
    }
    
    public void testNoIndirectSuperclass() {
        test("NoIndirectSuperclass", RelationType.NOINDIRECTSUPERCLASS);
    }
    
    public void testNoDirectSuperclassObject() {
        test("NoDirectSuperclassObject", RelationType.NODIRECTSUPERCLASS);
    }
    
    public void testNoIndirectSuperclassObject() {
        test("NoIndirectSuperclassObject", RelationType.NOINDIRECTSUPERCLASS);
    }
    
	private void test(String name, RelationType loc) {
		DetectionResults r = testProject(name);
        Assert.assertEquals(loc, r.getMetrics().amountPerRelation.keySet().iterator().next());
	}
}
