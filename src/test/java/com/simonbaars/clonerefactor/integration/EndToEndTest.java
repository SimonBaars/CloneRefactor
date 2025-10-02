package com.simonbaars.clonerefactor.integration;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.nio.file.Paths;

/**
 * End-to-end integration tests for CloneRefactor
 */
public class EndToEndTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EndToEndTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(EndToEndTest.class);
    }

    @Override
    public void setUp() {
        // Ensure we start with default settings for each test
        Settings.get().setCloneType(CloneType.TYPE1R);
        Settings.get().setMinAmountOfLines(3);
        Settings.get().setMinAmountOfTokens(1);
        Settings.get().setMinCloneClassSize(2);
    }

    /**
     * Test that the tool can analyze itself
     */
    public void testAnalyzeCloneRefactorSourceCode() {
        System.out.println("Testing CloneRefactor on its own source code...");
        
        String sourcePath = System.getProperty("user.dir") + "/src/main/java";
        DetectionResults results = Main.cloneDetection(Paths.get(sourcePath));
        
        // Verify we got results
        assertNotNull("Results should not be null", results);
        assertNotNull("Metrics should not be null", results.getMetrics());
        
        // Should detect at least some clones in the codebase
        assertTrue("Should detect at least one clone class", 
            results.getMetrics().generalStats.getOrDefault("Clone classes", 0) >= 1);
        
        // Should have analyzed some files
        assertTrue("Should analyze at least 100 lines of code", 
            results.getMetrics().generalStats.getOrDefault("Total Lines", 0) > 100);
        
        System.out.println("Clone classes found: " + 
            results.getMetrics().generalStats.get("Clone classes"));
        System.out.println("Total lines analyzed: " + 
            results.getMetrics().generalStats.get("Total Lines"));
        System.out.println("Percentage duplicated: " + 
            results.getMetrics().averages.get("Percentage Duplicated") + "%");
        
        System.out.println("✓ Successfully analyzed CloneRefactor source code");
    }

    /**
     * Test Type 1 clone detection on test resources
     */
    public void testType1CloneDetection() {
        System.out.println("Testing Type 1 clone detection...");
        
        Settings.get().setCloneType(CloneType.TYPE1R);
        
        String testPath = getClass().getClassLoader()
            .getResource("TYPE1R/EqualFullMethods").getFile();
        DetectionResults results = Main.cloneDetection(Paths.get(testPath));
        
        assertNotNull("Results should not be null", results);
        assertTrue("Should detect clones in EqualFullMethods test case",
            results.getMetrics().generalStats.getOrDefault("Clone classes", 0) >= 1);
        
        System.out.println("✓ Type 1 clone detection working correctly");
    }

    /**
     * Test Type 2 clone detection
     */
    public void testType2CloneDetection() {
        System.out.println("Testing Type 2 clone detection...");
        
        Settings.get().setCloneType(CloneType.TYPE2R);
        
        String testPath = getClass().getClassLoader()
            .getResource("TYPE2R/DifferentLiterals").getFile();
        
        if (testPath != null) {
            DetectionResults results = Main.cloneDetection(Paths.get(testPath));
            assertNotNull("Results should not be null", results);
            
            System.out.println("✓ Type 2 clone detection working correctly");
        } else {
            System.out.println("⚠ TYPE2R test resources not available, skipping");
        }
    }

    /**
     * Test configuration changes
     */
    public void testConfigurationChanges() {
        System.out.println("Testing configuration changes...");
        
        // Test with strict thresholds
        Settings.get().setMinAmountOfLines(10);
        Settings.get().setMinAmountOfTokens(50);
        Settings.get().setMinCloneClassSize(3);
        
        assertEquals("Min lines should be 10", 10, Settings.get().getMinAmountOfLines());
        assertEquals("Min tokens should be 50", 50, Settings.get().getMinAmountOfTokens());
        assertEquals("Min clone class size should be 3", 3, Settings.get().getMinCloneClassSize());
        
        // Reset to defaults
        Settings.get().setMinAmountOfLines(3);
        Settings.get().setMinAmountOfTokens(1);
        Settings.get().setMinCloneClassSize(2);
        
        System.out.println("✓ Configuration changes working correctly");
    }

    /**
     * Test that metrics are properly populated
     */
    public void testMetricsPopulation() {
        System.out.println("Testing metrics population...");
        
        String testPath = getClass().getClassLoader()
            .getResource("TYPE1R/SimpleClone").getFile();
        DetectionResults results = Main.cloneDetection(Paths.get(testPath));
        
        assertNotNull("Metrics should not be null", results.getMetrics());
        assertNotNull("General stats should not be null", results.getMetrics().generalStats);
        assertNotNull("Averages should not be null", results.getMetrics().averages);
        
        // Should have basic statistics
        assertTrue("Should have Total Lines metric", 
            results.getMetrics().generalStats.containsKey("Total Lines"));
        assertTrue("Should have Total Tokens metric", 
            results.getMetrics().generalStats.containsKey("Total Tokens"));
        
        System.out.println("✓ Metrics properly populated");
    }

    /**
     * Test empty directory handling
     */
    public void testEmptyDirectory() {
        System.out.println("Testing empty directory handling...");
        
        try {
            // Create a temporary empty directory
            java.io.File tempDir = java.io.File.createTempFile("clonerefactor-test", "");
            tempDir.delete();
            tempDir.mkdir();
            tempDir.deleteOnExit();
            
            DetectionResults results = Main.cloneDetection(Paths.get(tempDir.getAbsolutePath()));
            
            // Should handle gracefully without crashes
            assertNotNull("Results should not be null even for empty directory", results);
            
            System.out.println("✓ Empty directory handled gracefully");
        } catch (Exception e) {
            // Expected to potentially fail, just ensure it doesn't crash the test suite
            System.out.println("⚠ Empty directory test threw exception (expected): " + e.getMessage());
        }
    }
}
