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
    
    public void testJSONCollection() {
    	System.out.println("json-collection");
        String path = "/Users/sbaars/clone/git/json-collection/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    public void testSMSLib() {
    	System.out.println("smslib");
        String path = "/Users/sbaars/clone/git/smslib/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    public void testSpark() {
    	System.out.println("spark");
        String path = "/Users/sbaars/clone/git/spark/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
}
