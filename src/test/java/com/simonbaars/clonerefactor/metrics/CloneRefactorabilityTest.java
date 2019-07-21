package com.simonbaars.clonerefactor.metrics;

import com.simonbaars.clonerefactor.helper.Type1Test;
import com.simonbaars.clonerefactor.metrics.context.enums.Refactorability;
import com.simonbaars.clonerefactor.model.DetectionResults;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for the node locations.
 */
public class CloneRefactorabilityTest extends Type1Test {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CloneRefactorabilityTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( CloneRefactorabilityTest.class );
    }
    
    public void testFullMethod() {
        test("PartialBlock", Refactorability.PARTIALBLOCK);
    }
    
    public void testReturnAllFlows() {
        test("ReturnAllFlows", Refactorability.CANBEEXTRACTED);
    }
    
    public void testReturnNotAllFlows() {
        test("ReturnNotAllFlows", Refactorability.COMPLEXCONTROLFLOW);
    }
    
    public void testPartialMethod() {
        test("SimpleClone", Refactorability.CANBEEXTRACTED);
    }
    
    public void testSeveralMethods() {
        test("SeveralMethodsCloned", Refactorability.NOEXTRACTIONBYCONTENTTYPE);
    }
    
    public void testBreakInNonClonedLoop() {
        test("BreakInNonClonedLoop", Refactorability.COMPLEXCONTROLFLOW);
    }
    
    public void testBreakInClonedLoop() {
        test("BreakInClonedLoop", Refactorability.CANBEEXTRACTED);
    }
    
    public void testContinueInNonClonedLoop() {
        test("ContinueInNonClonedLoop", Refactorability.COMPLEXCONTROLFLOW);
    }
    
    public void testContinueInClonedLoop() {
        test("ContinueInClonedLoop", Refactorability.CANBEEXTRACTED);
    }
    
    public void testOverlaps() {
        test("EqualLinesSingleFile", Refactorability.OVERLAPS);
    }

	private void test(String name, Refactorability loc) {
		System.out.println(name);
		DetectionResults r = testProject(name);
		System.out.println(r);
        Assert.assertEquals(loc, r.getMetrics().amountPerExtract.keySet().iterator().next());
	}
}
