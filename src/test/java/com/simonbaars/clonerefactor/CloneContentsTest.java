package com.simonbaars.clonerefactor;

import com.simonbaars.clonerefactor.metrics.enums.CloneContents.ContentsType;
import com.simonbaars.clonerefactor.model.DetectionResults;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for the node locations.
 */
public class CloneContentsTest extends TestCase {

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
        test("HasClassDeclaraton", ContentsType.HASCLASSDECLARATION);
    }
    
    public void testHasEnumDeclaration() {
        test("HasEnumDeclaraton", ContentsType.HASENUMDECLARATION);
    }
    
    public void testHasInterfaceDeclaration() {
        test("HasInterfaceDeclaraton", ContentsType.HASINTERFACEDECLARATION);
    }
    
    public void testHasEnumFields() {
        test("HasEnumFields", ContentsType.HASENUMFIELDS);
    }

	private void test(String name, ContentsType loc) {
		DetectionResults r = testProject(name);
        Assert.assertEquals(loc, r.getMetrics().amountPerContents.keySet().iterator().next());
	}

	private DetectionResults testProject(String project) {
		return Main.cloneDetection(CloneContentsTest.class.getClassLoader().getResource(project).getFile());
	}
}
