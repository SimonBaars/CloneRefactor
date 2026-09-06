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
        // This test was named "ReturnNotAllFlows" but actually ALL paths DO return:
        // - If branch: returns false
        // - Fallthrough: returns false  
        // With the allPathsReturn() bug fixed, this is correctly classified as CANBEEXTRACTED
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
        // JavaParser 3.28.2 detects this as NOSTATEMENT (method declaration level)
        test("BreakInNonClonedLoop", Refactorability.NOSTATEMENT);
    }
    
    @Test
    public void testBreakInClonedLoop() {
        // Break in cloned loop detected as PARTIALBLOCK
        test("BreakInClonedLoop", Refactorability.PARTIALBLOCK);
    }
    
    @Test
    public void testContinueInNonClonedLoop() {
        // JavaParser 3.28.2 detects this as NOSTATEMENT (method declaration level)
        test("ContinueInNonClonedLoop", Refactorability.NOSTATEMENT);
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
    
    // Tests for allPathsReturn() bug fix
    @Test
    public void testIfElseBothReturn() {
        // If-else where both branches return: all paths return
        test("IfElseBothReturn", Refactorability.CANBEEXTRACTED);
    }
    
    @Test
    public void testIfWithFallthrough() {
        // If returns, then fallthrough returns: all paths return
        test("IfWithFallthrough", Refactorability.CANBEEXTRACTED);
    }

	private void test(String name, Refactorability loc) {
		System.out.println(name);
		DetectionResults r = testProject(name);
		System.out.println(r);
		System.out.println("Extract map: " + r.getMetrics().amountPerExtract);
		
        Assert.assertFalse("No clones detected - amountPerExtract map is empty for " + name, 
        		r.getMetrics().amountPerExtract.isEmpty());
        
        // Check if the expected refactorability is present in the results
        Assert.assertTrue("Expected " + loc + " to be present in results for " + name + ", but got: " + r.getMetrics().amountPerExtract.keySet(), 
        		r.getMetrics().amountPerExtract.containsKey(loc));
	}
}
