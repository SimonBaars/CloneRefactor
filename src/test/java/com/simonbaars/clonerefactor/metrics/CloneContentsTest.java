package com.simonbaars.clonerefactor.metrics;

import org.junit.Assert;
import org.junit.Test;

import com.simonbaars.clonerefactor.context.enums.ContentsType;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.helper.Type1Test;

/**
 * Unit test for the node locations.
 */
public class CloneContentsTest extends Type1Test {
    
    @Test
    public void testFullMethod() {
        test("EqualFullMethods", ContentsType.FULLMETHOD);
    }
    
    @Test
    public void testPartialMethod() {
        test("SingleFile", ContentsType.PARTIALMETHOD);
    }
    
    @Test
    public void testSeveralMethods() {
        test("SeveralMethodsCloned", ContentsType.SEVERALMETHODS);
    }
    
    @Test
    public void testOnlyFields() {
        test("OnlyFields", ContentsType.ONLYFIELDS);
    }
    
    @Test
    public void testFullClass() {
        test("FullClass", ContentsType.FULLCLASS);
    }
    
    @Test
    public void testFullEnum() {
        test("FullEnum", ContentsType.FULLENUM);
    }
    
    @Test
    public void testFullInterface() {
        test("FullInterface", ContentsType.FULLINTERFACE);
    }

	private void test(String name, ContentsType loc) {
		DetectionResults r = testProject(name);
		System.out.println("Detection results: " + r);
		System.out.println("Metrics: " + r.getMetrics());
		System.out.println("Contents map: " + r.getMetrics().amountPerContents);
		
        Assert.assertFalse("No clones detected - amountPerContents map is empty for " + name, 
        		r.getMetrics().amountPerContents.isEmpty());
        		
        ContentsType actualType = r.getMetrics().amountPerContents.keySet().iterator().next();
        Assert.assertEquals("Expected " + loc + " but got " + actualType, loc, actualType);
	}
}
