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
public class SimpleTest extends TestCase {
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
    public SimpleTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( SimpleTest.class );
    }
    
    public void testCustom() {
    	System.out.println("custom");
        List<Sequence> chains = Main.cloneDetection("/Users/sbaars/Downloads/java_projects/ardublock");
        printNice(chains);
    }

    /**
     * Test for clones that consist of lines that do not occur elsewhere.
     */
    public void testSimpleClones() {
    	System.out.println("testSimpleClones");
        List<Sequence> chains = testProject(SIMPLE_PROJECT);
        printNice(chains);
    }
    
	/**
     * Test for clones that consist of all equal lines.
     */
    public void testEqualLines() {
    	System.out.println("testEqualLines");
    	List<Sequence> chains = testProject(EQUAL_LINES_PROJECT);
    	printNice(chains);
    }
    
    /**
     * Test for three clones, of which one starts a line later than the others.
     */
    public void testPartialClonesLeft() {
    	System.out.println("testPartialClonesLeft");
    	List<Sequence> chains = testProject(PARTIAL_CLONES_LEFT);
    	printNice(chains);
    }
    
    /**
     * Test for three clones, of which one ends a line later than the others.
     */
    public void testPartialLinesRight() {
    	System.out.println("testPartialLinesRight");
    	List<Sequence> chains = testProject(PARTIAL_CLONES_RIGHT);
    	printNice(chains);
    }
    
    private void printNice(List<Sequence> chains) {
    	System.out.println(Arrays.toString(chains.toArray()).replace("Location [", "\nLocation [").replace("Sequence [sequence=[", "\nSequence [sequence=["));
	}

	/**
     * Test for clones in Java enumerations.
     */
    public void testEnumClone() {
    	System.out.println("testEnumClone");
    	List<Sequence> chains = testProject(ENUM_PROJECT);
    	printNice(chains);
    }
    
    /**
     * Test for clones in a single file, with just a single line to separate the clones.
     */
    public void testSingleFile() {
    	System.out.println("testSingleFile");
    	List<Sequence> chains = testProject(SINGLE_FILE_PROJECT);
    	printNice(chains);
    }
    
    
    /**
     * Test for clones that differ in length but consist of lines with equal tokens.
     */
    public void testUnequalSizeClones() {
    	System.out.println("testUnequalSizeClones");
    	List<Sequence> chains = testProject(UNEQUAL_SIZE_CLONES_PROJECT);
    	printNice(chains);
    }
    
    /**
     * Test for clones that span multiple methods.
     */
    public void testSeveralMethodsCloned() {
    	System.out.println("testSeveralMethodsCloned");
    	List<Sequence> chains = testProject(SEVERAL_METHODS_PROJECT);
    	printNice(chains);
    }
    
    /**
     * Test for clones in import statements.
     */
    public void testImportStatements() {
    	System.out.println("testImportStatements");
    	List<Sequence> chains = testProject("EqualImportStatements");
    	printNice(chains);
        List<Sequence> expectedChains = new ArrayList<>();
        Assert.assertTrue(checkArbitraryOrder(chains, expectedChains));
    }

	private List<Sequence> testProject(String project) {
		return Main.cloneDetection(SimpleTest.class.getClassLoader().getResource(project).getFile());
	}
    
	private File getJavaFileFromProject(String project, String file) {
		return new File(SimpleTest.class.getClassLoader().getResource(project+File.separator+file+".java").getFile());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> boolean checkArbitraryOrder(List<T> chains, List<T> expectedChains) {
		if(chains.size()!=expectedChains.size())
			return false;
		
		outerloop: for(Object chain : chains) {
			for(Object expectedChain : expectedChains) {
				if((chain instanceof List && checkArbitraryOrder((List)chain, (List)expectedChain)) ||
					(chain instanceof Sequence && checkArbitraryOrder(((Sequence)chain).getSequence(), ((Sequence)expectedChain).getSequence())) ||
					(chain instanceof Location && ((Location)chain).isSame((Location)expectedChain)) || 
					(chain instanceof Object && chain.equals(expectedChain))){
					continue outerloop;
				}
			}
			return false;
		}
		return true;
	}
}
