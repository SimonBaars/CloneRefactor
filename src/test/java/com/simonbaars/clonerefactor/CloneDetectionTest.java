package com.simonbaars.clonerefactor;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for the clone detector.
 */
public class CloneDetectionTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CloneDetectionTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( CloneDetectionTest.class );
    }

    /**
     * Test for clones that consist of lines that do not occur elsewhere.
     */
    public void simpleClones()
    {
        testProject("SimpleClone");
    }
    
    /**
     * Test for clones that consist of all equal lines.
     */
    public void equalLines()
    {
        testProject("EqualLines");
    }
    
    /**
     * Test for three clones, of which one starts a line later than the others.
     */
    public void partialClonesLeft()
    {
        testProject("PartialLinesLeft");
    }
    
    /**
     * Test for three clones, of which one ends a line later than the others.
     */
    public void partialLinesRight()
    {
        testProject("PartialLinesRight");
    }
    
    /**
     * Test for clones in Java enumerations.
     */
    public void enumClone()
    {
        testProject("EnumClone");
    }
    

	private void testProject(String project) {
		Main.cloneDetection(CloneDetectionTest.class.getClassLoader().getResource(project).getFile());
	}
    
    
}
