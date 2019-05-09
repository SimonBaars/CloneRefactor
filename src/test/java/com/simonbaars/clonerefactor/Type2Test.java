package com.simonbaars.clonerefactor;

import java.io.File;
import java.nio.file.Paths;

import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.thread.CorpusThread;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for the clone detector.
 */
public class Type2Test extends TestCase {
    private static final String SEVERAL_METHODS_PROJECT = "SeveralMethodsCloned";
	private static final String UNEQUAL_SIZE_CLONES_PROJECT = "UnequalSizeClones";
	private static final String SINGLE_FILE_PROJECT = "SingleFile";
	private static final String PARTIAL_CLONES_LEFT = "PartialClonesLeft";
	private static final String PARTIAL_CLONES_RIGHT = "PartialClonesRight";
	private static final String SIMPLE_PROJECT = "SimpleClone";
    private static final String EQUAL_LINES_PROJECT = "EqualLines";
    private static final String ENUM_PROJECT = "EnumClone";
    

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public Type2Test( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(Type2Test.class);
    }
    
    public void testDifferentLiterals() {
    	System.out.println("testDifferentLiterals");
    	System.out.println(testProject("DifferentLiterals"));
    }
    
    public void testDifferentMethods() {
    	System.out.println("testDifferentMethods");
    	System.out.println(testProject("DifferentMethods"));
    }

	private DetectionResults testProject(String project) {
		return Main.cloneDetection(Type2Test.class.getClassLoader().getResource("Type2-"+project).getFile()).sorted();
	}
}
