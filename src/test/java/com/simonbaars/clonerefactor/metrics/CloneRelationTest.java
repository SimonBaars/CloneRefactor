package com.simonbaars.clonerefactor.metrics;

import org.junit.Assert;
import org.junit.Test;

import com.simonbaars.clonerefactor.context.enums.RelationType;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.helper.Type1Test;

/**
 * Unit test for the node locations.
 */
public class CloneRelationTest extends Type1Test {
    
    @Test
    public void testAncestor() {
        test("Ancestor", RelationType.ANCESTOR);
    }
    
    @Test
    public void testFirstCousin() {
        test("FirstCousin", RelationType.FIRSTCOUSIN);
    }
    
    @Test
    public void testSibling() {
        test("Sibling", RelationType.SIBLING);
    }
    
    @Test
    public void testSuperClass() {
        test("SuperClass", RelationType.SUPERCLASS);
    }
    
    @Test
    public void testMethod() {
        test("SingleFile", RelationType.SAMEMETHOD);
    }
    
    @Test
    public void testExternalAncestor() {
        test("ExternalAncestor", RelationType.EXTERNALANCESTOR);
    }
    
    @Test
    public void testSameClass() {
        test("SameClass", RelationType.SAMECLASS);
    }
    
    @Test
    public void testSameExternalSuperClass() {
        test("ExternalSuperClass", RelationType.EXTERNALSUPERCLASS);
    }
    
    @Test
    public void testSameHierarchy() {
        test("SameHierarchy", RelationType.COMMONHIERARCHY);
    }
    
    @Test
    public void testSameInterface() {
        test("SameInterface", RelationType.SAMEDIRECTINTERFACE);
    }
    
    @Test
    public void testSameInterfaceInSuperclass() {
        test("SameInterfaceInSuperclass", RelationType.SAMEINDIRECTINTERFACE);
    }

    @Test
    public void testSameInterfaceInInterfaceHierarchy() {
        test("SameInterfaceInInterfaceHierarchy", RelationType.SAMEINDIRECTINTERFACE);
    }
    
    @Test
    public void testNoDirectSuperclass() {
        test("SimpleClone", RelationType.NODIRECTSUPERCLASS);
    }
    
    @Test
    public void testNoIndirectSuperclass() {
        test("NoIndirectSuperclass", RelationType.NOINDIRECTSUPERCLASS);
    }
    
    @Test
    public void testNoDirectSuperclassObject() {
        test("NoDirectSuperclassObject", RelationType.NODIRECTSUPERCLASS);
    }
    
    @Test
    public void testNoIndirectSuperclassObject() {
        test("NoIndirectSuperclassObject", RelationType.NOINDIRECTSUPERCLASS);
    }
    
	private void test(String name, RelationType loc) {
		System.out.println(name);
		DetectionResults r = testProject(name);
        Assert.assertEquals(loc, r.getMetrics().amountPerRelation.keySet().iterator().next());
	}
}
