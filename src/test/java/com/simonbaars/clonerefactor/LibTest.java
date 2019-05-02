package com.simonbaars.clonerefactor;

import java.nio.file.Paths;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Here we use libraries to test projects.
 */
public class LibTest extends TestCase {
    

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public LibTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( LibTest.class );
    }
    
    public void testCustom() {
    	System.out.println("custom");
        String path = "/Users/sbaars/clone/git/json-collection/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
}
