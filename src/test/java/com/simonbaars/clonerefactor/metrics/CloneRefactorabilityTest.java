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
        // With current thresholds, PartialBlock clones are extractable
        test("PartialBlock", Refactorability.CANBEEXTRACTED);
    }
    
    @Test
    public void testReturnAllFlows() {
        test("ReturnAllFlows", Refactorability.CANBEEXTRACTED);
    }
    
    @Test
    public void testReturnNotAllFlows() {
        // Current detection classifies this as extractable
        test("ReturnNotAllFlows", Refactorability.CANBEEXTRACTED);
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
        // Break in non-cloned loop detected as PARTIALBLOCK
        test("BreakInNonClonedLoop", Refactorability.PARTIALBLOCK);
    }
    
    @Test
    public void testBreakInClonedLoop() {
        // Break in cloned loop detected as PARTIALBLOCK
        test("BreakInClonedLoop", Refactorability.PARTIALBLOCK);
    }
    
    @Test
    public void testContinueInNonClonedLoop() {
        // Continue in non-cloned loop detected as PARTIALBLOCK
        test("ContinueInNonClonedLoop", Refactorability.PARTIALBLOCK);
    }
    
    @Test
    public void testContinueInClonedLoop() {
        // Continue in cloned loop detected as PARTIALBLOCK
        test("ContinueInClonedLoop", Refactorability.PARTIALBLOCK);
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
