package com.simonbaars.clonerefactor;

import com.simonbaars.clonerefactor.model.DetectionResults;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for the clone detector.
 */
public class Type3Test extends TestCase {    

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public Type3Test( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(Type3Test.class);
    }
    
    public void testSimpleClone() {
    	System.out.println("testSimpleClone");
    	System.out.println(testProject("SimpleClone"));
    }

	private DetectionResults testProject(String project) {
		return Main.cloneDetection(Type3Test.class.getClassLoader().getResource("Type3"+project).getFile()).sorted();
	}
}
