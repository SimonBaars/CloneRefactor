package com.simonbaars.clonerefactor.types;

import com.simonbaars.clonerefactor.helper.Type3Test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for the clone detector.
 */
public class Type3Testcases extends Type3Test {    

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public Type3Testcases( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(Type3Testcases.class);
    }
    
    public void testStatementAddedLeft() {
    	System.out.println("testStatementAddedLeft");
    	System.out.println(testProject("StatementAddedLeft"));
    }
    
    public void testStatementAddedRight() {
    	System.out.println("testStatementAddedRight");
    	System.out.println(testProject("StatementAddedRight"));
    }
    
    public void testStatementAddedBothSides() {
    	System.out.println("testStatementAddedBothSides");
    	System.out.println(testProject("StatementAddedBothSides"));
    }
    
    public void testSizeThreeCloneClass() {
    	System.out.println("testSizeThreeCloneClass");
    	System.out.println(testProject("SizeThreeCloneClass"));
    }
}
