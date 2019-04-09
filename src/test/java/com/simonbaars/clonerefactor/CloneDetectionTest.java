package com.simonbaars.clonerefactor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        List<Sequence> chains = testProject(SIMPLE_PROJECT);
        System.out.println(Arrays.toString(chains.toArray()));
        Sequence c = new Sequence();
        c.add(new Location(getJavaFileFromProject(SIMPLE_PROJECT, "Clone1"), 5, 16));
        c.add(new Location(getJavaFileFromProject(SIMPLE_PROJECT, "Clone2"), 5, 16));
        List<Sequence> expectedChains = new ArrayList<>();
        expectedChains.add(c);
        Assert.assertTrue(checkArbitraryOrder(chains, expectedChains));
    }
    
	/**
     * Test for clones that consist of all equal lines.
     */
    public void testEqualLines() {
    	List<Sequence> chains = testProject(EQUAL_LINES_PROJECT);
    	System.out.println(Arrays.toString(chains.toArray()));
        Sequence c = new Sequence();
        c.add(new Location(getJavaFileFromProject(EQUAL_LINES_PROJECT, "Clone1"), 5, 16));
        c.add(new Location(getJavaFileFromProject(EQUAL_LINES_PROJECT, "Clone2"), 5, 16));
        List<Sequence> expectedChains = new ArrayList<>();
        expectedChains.add(c);
        Assert.assertTrue(checkArbitraryOrder(chains, expectedChains));
    }
    
    /**
     * Test for three clones, of which one starts a line later than the others.
     */
    public void testPartialClonesLeft() {
    	List<Sequence> chains = testProject(PARTIAL_CLONES_LEFT);
    	System.out.println(Arrays.toString(chains.toArray()));
    	Sequence c = new Sequence();
        c.add(new Location(getJavaFileFromProject(PARTIAL_CLONES_LEFT, "Clone1"), 6, 16));
        c.add(new Location(getJavaFileFromProject(PARTIAL_CLONES_LEFT, "Clone2"), 6, 16));
        c.add(new Location(getJavaFileFromProject(PARTIAL_CLONES_LEFT, "Clone3"), 5, 15));
        Sequence c2 = new Sequence();
        c2.add(new Location(getJavaFileFromProject(PARTIAL_CLONES_LEFT, "Clone1"), 5, 16));
        c2.add(new Location(getJavaFileFromProject(PARTIAL_CLONES_LEFT, "Clone2"), 5, 16));
        List<Sequence> expectedChains = new ArrayList<>();
        expectedChains.add(c);
        expectedChains.add(c2);
        Assert.assertTrue(checkArbitraryOrder(chains, expectedChains));
    }
    
    /**
     * Test for three clones, of which one ends a line later than the others.
     */
    public void testPartialLinesRight() {
    	List<Sequence> chains = testProject(PARTIAL_CLONES_RIGHT);
    	System.out.println(Arrays.toString(chains.toArray()));
    	Sequence c = new Sequence();
        c.add(new Location(getJavaFileFromProject(PARTIAL_CLONES_RIGHT, "Clone1"), 5, 15));
        c.add(new Location(getJavaFileFromProject(PARTIAL_CLONES_RIGHT, "Clone2"), 5, 15));
        c.add(new Location(getJavaFileFromProject(PARTIAL_CLONES_RIGHT, "Clone3"), 5, 15));
        Sequence c2 = new Sequence();
        c2.add(new Location(getJavaFileFromProject(PARTIAL_CLONES_RIGHT, "Clone1"), 5, 16));
        c2.add(new Location(getJavaFileFromProject(PARTIAL_CLONES_RIGHT, "Clone2"), 5, 16));
        List<Sequence> expectedChains = new ArrayList<>();
        expectedChains.add(c);
        expectedChains.add(c2);
        Assert.assertTrue(checkArbitraryOrder(chains, expectedChains));
    }
    
    /**
     * Test for clones in Java enumerations.
     */
    public void testEnumClone() {
    	List<Sequence> chains = testProject(ENUM_PROJECT);
    	System.out.println(Arrays.toString(chains.toArray()));
    	Sequence c = new Sequence();
        c.add(new Location(getJavaFileFromProject(ENUM_PROJECT, "Clone1"), 4, 23));
        c.add(new Location(getJavaFileFromProject(ENUM_PROJECT, "Clone2"), 4, 23));
        List<Sequence> expectedChains = new ArrayList<>();
        expectedChains.add(c);
        Assert.assertTrue(checkArbitraryOrder(chains, expectedChains));
    }
    
    /**
     * Test for clones in a single file, with just a single line to separate the clones.
     */
    public void testSingleFile() {
    	List<Sequence> chains = testProject(SINGLE_FILE_PROJECT);
    	System.out.println(Arrays.toString(chains.toArray()));
    	Sequence c = new Sequence();
        c.add(new Location(getJavaFileFromProject(SINGLE_FILE_PROJECT, "Clone1"), 5, 14));
        c.add(new Location(getJavaFileFromProject(SINGLE_FILE_PROJECT, "Clone1"), 16, 25));
        List<Sequence> expectedChains = new ArrayList<>();
        expectedChains.add(c);
        Assert.assertTrue(checkArbitraryOrder(chains, expectedChains));
    }
    
    
    /**
     * Test for clones that differ in length but consist of lines with equal tokens.
     */
    public void testUnequalSizeClones() {
    	List<Sequence> chains = testProject(UNEQUAL_SIZE_CLONES_PROJECT);
    	System.out.println(Arrays.toString(chains.toArray()));
        Sequence c = new Sequence();
        c.add(new Location(getJavaFileFromProject(UNEQUAL_SIZE_CLONES_PROJECT, "Clone1"), 5, 16)); //This is not entirely correct, but the question is whether it matters?
        c.add(new Location(getJavaFileFromProject(UNEQUAL_SIZE_CLONES_PROJECT, "Clone2"), 6, 17));
        List<Sequence> expectedChains = new ArrayList<>();
        expectedChains.add(c);
        Assert.assertTrue(checkArbitraryOrder(chains, expectedChains));
    }
    
    /**
     * Test for clones that span multiple methods.
     */
    public void testSeveralMethodsCloned() {
    	List<Sequence> chains = testProject(SEVERAL_METHODS_PROJECT);
    	System.out.println(Arrays.toString(chains.toArray()));
        Sequence c = new Sequence();
        c.add(new Location(getJavaFileFromProject(SEVERAL_METHODS_PROJECT, "Clone1"), 4, 13)); //This is not entirely correct, but the question is whether it matters?
        c.add(new Location(getJavaFileFromProject(SEVERAL_METHODS_PROJECT, "Clone2"), 4, 13));
        List<Sequence> expectedChains = new ArrayList<>();
        expectedChains.add(c);
        Assert.assertTrue(checkArbitraryOrder(chains, expectedChains));
    }

	private List<Sequence> testProject(String project) {
		return Main.cloneDetection(CloneDetectionTest.class.getClassLoader().getResource(project).getFile());
	}
    
	private File getJavaFileFromProject(String project, String file) {
		return new File(CloneDetectionTest.class.getClassLoader().getResource(project+File.separator+file+".java").getFile());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> boolean checkArbitraryOrder(List<T> chains, List<T> expectedChains) {
		if(chains.size()!=expectedChains.size())
			return false;
		
		outerloop: for(Object chain : chains) {
			for(Object expectedChain : expectedChains) {
				if((chain instanceof List && checkArbitraryOrder((List)chain, (List)expectedChain)) ||
					(chain instanceof Sequence && checkArbitraryOrder(((Sequence)chain).getSequence(), ((Sequence)expectedChain).getSequence())) ||
					(chain instanceof Object && chain.equals(expectedChain))){
					continue outerloop;
				}
			}
			return false;
		}
		return true;
	}
}
