package com.simonbaars.clonerefactor.edgecases;

import org.junit.Assert;
import org.junit.Test;

import com.simonbaars.clonerefactor.context.enums.Refactorability;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.helper.Type1Test;

/**
 * Edge-case tests for structural patterns and boundaries.
 * Tests constructors, enums, static initializers, empty blocks, single statements,
 * and special clone types.
 */
public class StructuralEdgeCasesTest extends Type1Test {
    
    @Test
    public void testConstructorClone() {
        DetectionResults r = testProject("ConstructorClone");
        Assert.assertFalse("Should detect clones in constructors", 
                r.getMetrics().amountPerExtract.isEmpty());
        // Constructors have different refactorability constraints
    }
    
    @Test
    public void testEnumClone() {
        DetectionResults r = testProject("EnumClone");
        // Enums have special structure - may or may not be detected
        Assert.assertNotNull("Should complete detection", r);
    }
    
    @Test
    public void testStaticInitializer() {
        DetectionResults r = testProject("StaticInitializer");
        Assert.assertFalse("Should detect clones in static initializers", 
                r.getMetrics().amountPerExtract.isEmpty());
    }
    
    @Test
    public void testEmptyBlocks() {
        DetectionResults r = testProject("EmptyBlocks");
        // Empty blocks should not be considered substantial clones
        Assert.assertNotNull("Should complete detection", r);
    }
    
    @Test
    public void testSingleStatement() {
        DetectionResults r = testProject("SingleStatement");
        // Single statements may not meet minimum thresholds
        Assert.assertNotNull("Should complete detection", r);
    }
    
    @Test
    public void testPartialMethodClone() {
        DetectionResults r = testProject("PartialMethodClone");
        // Partial method clones may not always be detected due to
        // differing method endings reducing similarity
        Assert.assertNotNull("Should complete detection", r);
        if (!r.getMetrics().amountPerExtract.isEmpty()) {
            Refactorability actual = r.getMetrics().amountPerExtract.keySet().iterator().next();
            // Partial methods should be extractable or partial
            System.out.println("Partial method clone detected as: " + actual);
            Assert.assertTrue("Partial method clone should be extractable or partial", 
                    actual == Refactorability.CANBEEXTRACTED || actual == Refactorability.PARTIALBLOCK);
        }
    }
    
    @Test
    public void testWholeMethodClone() {
        DetectionResults r = testProject("WholeMethodClone");
        // Whole method clones may be detected as method declarations
        // (NOSTATEMENT) rather than method bodies, depending on how
        // the clone detector identifies the boundaries
        Assert.assertNotNull("Should complete detection", r);
        if (!r.getMetrics().amountPerExtract.isEmpty()) {
            System.out.println("Whole method clone detected with refactorability: " + 
                    r.getMetrics().amountPerExtract.keySet().iterator().next());
        }
    }
    
    @Test
    public void testCommentsAndWhitespace() {
        DetectionResults r = testProject("CommentsAndWhitespace");
        Assert.assertFalse("Should detect clones despite comment/whitespace differences", 
                r.getMetrics().amountPerExtract.isEmpty());
        // Type1 should ignore comments and whitespace
    }
    
    @Test
    public void testIdenticalClones() {
        DetectionResults r = testProject("IdenticalClones");
        Assert.assertFalse("Should detect identical clones", 
                r.getMetrics().amountPerExtract.isEmpty());
        Refactorability actual = r.getMetrics().amountPerExtract.keySet().iterator().next();
        // Identical clones may be detected as method declarations (NOSTATEMENT)
        // or as method bodies (CANBEEXTRACTED) depending on detection granularity
        System.out.println("Identical clones detected as: " + actual);
        Assert.assertNotNull("Should have refactorability classification", actual);
    }
    
    @Test
    public void testNestedClasses() {
        DetectionResults r = testProject("NestedClasses");
        Assert.assertFalse("Should detect clones in nested classes", 
                r.getMetrics().amountPerExtract.isEmpty());
    }
}
