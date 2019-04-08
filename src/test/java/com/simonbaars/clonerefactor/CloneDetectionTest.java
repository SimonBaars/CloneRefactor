package com.simonbaars.clonerefactor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.simonbaars.clonerefactor.model.Chain;
import com.simonbaars.clonerefactor.model.Location;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for the clone detector.
 */
public class CloneDetectionTest extends TestCase
{
    private static final String SIMPLE_PROJECT = "SimpleClone";
    private static final String EQUAL_LINES_PROJECT = "EqualLines";

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
    public void testSimpleClones()
    {
        List<Chain> chains = testProject(SIMPLE_PROJECT);
        Chain c = new Chain();
        c.add(new Location(getJavaFileFromProject(SIMPLE_PROJECT, "Clone1"), 5, 16));
        c.add(new Location(getJavaFileFromProject(SIMPLE_PROJECT, "Clone2"), 5, 16));
        List<Chain> expectedChains = new ArrayList<>();
        expectedChains.add(c);
        Assert.assertTrue(checkArbitraryOrder(chains, expectedChains));
    }
    
	/**
     * Test for clones that consist of all equal lines.
     */
    public void testEqualLines()
    {
    	List<Chain> chains = testProject(EQUAL_LINES_PROJECT);
        Chain c = new Chain();
        c.add(new Location(getJavaFileFromProject(EQUAL_LINES_PROJECT, "Clone1"), 5, 16));
        c.add(new Location(getJavaFileFromProject(EQUAL_LINES_PROJECT, "Clone2"), 5, 16));
        List<Chain> expectedChains = new ArrayList<>();
        expectedChains.add(c);
        Assert.assertTrue(checkArbitraryOrder(chains, expectedChains));
    }
    
    /**
     * Test for three clones, of which one starts a line later than the others.
     */
    public void testPartialClonesLeft()
    {
        testProject("PartialClonesLeft");
    }
    
    /**
     * Test for three clones, of which one ends a line later than the others.
     */
    public void testPartialLinesRight()
    {
        testProject("PartialClonesRight");
    }
    
    /**
     * Test for clones in Java enumerations.
     */
    public void testEnumClone()
    {
        testProject("EnumClone");
    }
    

	private List<Chain> testProject(String project) {
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
					(chain instanceof Chain && checkArbitraryOrder(((Chain)chain).getChain(), ((Chain)expectedChain).getChain())) ||
					(chain instanceof Object && chain.equals(expectedChain))){
					continue outerloop;
				}
			}
			return false;
		}
		return true;
	}
}
