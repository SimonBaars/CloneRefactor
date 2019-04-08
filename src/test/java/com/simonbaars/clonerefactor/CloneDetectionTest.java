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
     * Rigourous Test :-)
     */
    public void testJPacman()
    {
        //Main.main(new String[] {"/Users/sbaars/clone/jpacman-framework"});
        Main.main(new String[] {CloneDetectionTest.class.getClassLoader().getResource("SimpleClone").getFile()});
    }
    
    
}
