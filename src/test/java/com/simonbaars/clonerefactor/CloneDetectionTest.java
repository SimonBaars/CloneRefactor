package com.simonbaars.clonerefactor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.model.Location;
import com.simonbaars.clonerefactor.model.Sequence;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for the clone detector.
 */
public class CloneDetectionTest extends TestCase {
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
    public CloneDetectionTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( CloneDetectionTest.class );
    }

    /**
     * Test for clones that consist of lines that do not occur elsewhere.
     */
    public void testSimpleClones() {
    	System.out.println("testSimpleClones");
        List<Sequence> chains = testProject(SIMPLE_PROJECT);
        System.out.println(Arrays.toString(chains.toArray()));
        Sequence c = new Sequence();
        c.add(new Location(getJavaFileFromProject(SIMPLE_PROJECT, "Clone2"), new Range(new Position(5, 3), new Position(16,38))));
        c.add(new Location(getJavaFileFromProject(SIMPLE_PROJECT, "Clone1"), new Range(new Position(5, 3), new Position(16,38))));
        List<Sequence> expectedChains = new ArrayList<>();
        expectedChains.add(c);
        Assert.assertEquals(expectedChains, chains);
    }
    
    /**
     * Test for clones in import statements (We don't want any).
     */
    public void testImportStatements() {
    	System.out.println("testImportStatements");
    	List<Sequence> chains = testProject("EqualImportStatements");
    	System.out.println(Arrays.toString(chains.toArray()));
        List<Sequence> expectedChains = new ArrayList<>();
        Assert.assertEquals(expectedChains, chains);
    }

	private List<Sequence> testProject(String project) {
		return Main.cloneDetection(CloneDetectionTest.class.getClassLoader().getResource(project).getFile());
	}
    
	private File getJavaFileFromProject(String project, String file) {
		return new File(CloneDetectionTest.class.getClassLoader().getResource(project+File.separator+file+".java").getFile());
	}
}
