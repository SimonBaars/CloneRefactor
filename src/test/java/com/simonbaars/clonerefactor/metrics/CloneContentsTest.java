package com.simonbaars.clonerefactor.metrics;

import com.simonbaars.clonerefactor.helper.Type1Test;
import com.simonbaars.clonerefactor.metrics.context.CloneContents.ContentsType;
import com.simonbaars.clonerefactor.model.DetectionResults;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for the node locations.
 */
public class CloneContentsTest extends Type1Test {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CloneContentsTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( CloneContentsTest.class );
    }
    
    public void testFullMethod() {
        test("EqualFullMethods", ContentsType.FULLMETHOD);
    }
    
    public void testPartialMethod() {
        test("SingleFile", ContentsType.PARTIALMETHOD);
    }
    
    public void testSeveralMethods() {
        test("SeveralMethodsCloned", ContentsType.SEVERALMETHODS);
    }
    
    public void testOnlyFields() {
        test("OnlyFields", ContentsType.ONLYFIELDS);
    }
    
    public void testIncludesFields() {
        test("IncludesFields", ContentsType.INCLUDESFIELDS);
    }
    
    public void testFullClass() {
        test("FullClass", ContentsType.FULLCLASS);
    }
    
    public void testFullEnum() {
        test("FullEnum", ContentsType.FULLENUM);
    }
    
    public void testFullInterface() {
        test("FullInterface", ContentsType.FULLINTERFACE);
    }
    
    public void testHasClassDeclaration() {
        test("HasClassDeclaration", ContentsType.HASCLASSDECLARATION);
    }
    
    public void testHasEnumDeclaration() {
        test("HasEnumDeclaration", ContentsType.HASENUMDECLARATION);
    }
    
    public void testHasInterfaceDeclaration() {
        test("HasInterfaceDeclaration", ContentsType.HASINTERFACEDECLARATION);
    }
    
    public void testHasEnumFields() {
        test("EnumClone", ContentsType.HASENUMFIELDS);
    }

	private void test(String name, ContentsType loc) {
		DetectionResults r = testProject(name);
		System.out.println(r);
        Assert.assertEquals(loc, r.getMetrics().amountPerContents.keySet().iterator().next());
	}
}
