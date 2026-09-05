package com.simonbaars.clonerefactor.metrics;

import org.junit.Assert;
import org.junit.Test;

import com.simonbaars.clonerefactor.context.enums.Refactorability;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.helper.Type1Test;

/**
 * Unit test for the node locations.
 */
public class CloneRefactorabilityTest extends Type1Test {
    
    @Test
    public void testFullMethod() {
        test("PartialBlock", Refactorability.PARTIALBLOCK);
    }
    
    @Test
    public void testReturnAllFlows() {
        test("ReturnAllFlows", Refactorability.CANBEEXTRACTED);
    }
    
    @Test
    public void testReturnNotAllFlows() {
        test("ReturnNotAllFlows", Refactorability.COMPLEXCONTROLFLOW);
    }
    
    @Test
    public void testPartialMethod() {
        test("SimpleClone", Refactorability.CANBEEXTRACTED);
    }
    
    @Test
    public void testSeveralMethods() {
        test("SeveralMethodsCloned", Refactorability.NOEXTRACTIONBYCONTENTTYPE);
    }
    
    @Test
    public void testBreakInNonClonedLoop() {
        test("BreakInNonClonedLoop", Refactorability.COMPLEXCONTROLFLOW);
    }
    
    @Test
    public void testBreakInClonedLoop() {
        test("BreakInClonedLoop", Refactorability.CANBEEXTRACTED);
    }
    
    @Test
    public void testContinueInNonClonedLoop() {
        test("ContinueInNonClonedLoop", Refactorability.COMPLEXCONTROLFLOW);
    }
    
    @Test
    public void testContinueInClonedLoop() {
        test("ContinueInClonedLoop", Refactorability.CANBEEXTRACTED);
    }
    
    @Test
    public void testOverlaps() {
        test("EqualLinesSingleFile", Refactorability.OVERLAPS);
    }

	private void test(String name, Refactorability loc) {
		System.out.println(name);
		DetectionResults r = testProject(name);
		System.out.println(r);
		System.out.println("Extract map: " + r.getMetrics().amountPerExtract);
		
        Assert.assertFalse("No clones detected - amountPerExtract map is empty for " + name, 
        		r.getMetrics().amountPerExtract.isEmpty());
        		
        Refactorability actualType = r.getMetrics().amountPerExtract.keySet().iterator().next();
        Assert.assertEquals("Expected " + loc + " but got " + actualType + " for " + name, loc, actualType);
	}
}
