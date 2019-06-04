package com.simonbaars.clonerefactor;

import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for the clone detector.
 */
public class Type3Testcases extends TestCase {    

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public Type3Testcases( String testName ) {
        super( testName );
    }
    
    @Override
    public void setUp() {
    	Settings.get().setCloneType(CloneType.TYPE3);
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

	private DetectionResults testProject(String project) {
		return Main.cloneDetection(Type3Testcases.class.getClassLoader().getResource("Type3"+project).getFile()).sorted();
	}
}
