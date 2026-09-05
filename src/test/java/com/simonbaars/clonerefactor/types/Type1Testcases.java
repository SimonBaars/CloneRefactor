package com.simonbaars.clonerefactor.types;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.core.util.SavePaths;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.helper.Type1Test;
import com.simonbaars.clonerefactor.scripts.model.MetricsTable;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Scope;
import com.simonbaars.clonerefactor.settings.Settings;

/**
 * Unit test for the clone detector.
 */
public class Type1Testcases extends Type1Test {
    private static final String SEVERAL_METHODS_PROJECT = "SeveralMethodsCloned";
	private static final String UNEQUAL_SIZE_CLONES_PROJECT = "UnequalSizeClones";
	private static final String SINGLE_FILE_PROJECT = "SingleFile";
	private static final String PARTIAL_CLONES_LEFT = "PartialClonesLeft";
	private static final String PARTIAL_CLONES_RIGHT = "PartialClonesRight";
	private static final String SIMPLE_PROJECT = "SimpleClone";
    private static final String EQUAL_LINES_PROJECT = "EqualLines";
    private static final String ENUM_PROJECT = "EnumClone";
    
    @Test
    @Ignore("Requires external repository at /home/simon/clone/git/kryo-serializers/")
    public void testMetricTables() {
    	MetricsTable tables = new MetricsTable();
    	System.out.println("kryo-serializers");
    	String path = "/home/simon/clone/git/kryo-serializers/";
    	System.out.println(Settings.get());
		tables.reportMetrics("Kryo", Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).getMetrics());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/cccc-plugin/")
    public void testCCCC() {
    	MetricsTable tables = new MetricsTable();
    	System.out.println("cccc-plugin");
    	String path = "/Users/sbaars/clone/git/cccc-plugin/";
    	System.out.println(Settings.get());
		tables.reportMetrics("Kryo", Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).getMetrics());
    }
    
    @Test
    @Ignore("Requires external repository at /home/simon/clone/git/zkfiddle-sandbox/")
    public void testMetricTables2() {
    	MetricsTable tables = new MetricsTable();
    	System.out.println("joda-time");
    	String path = "/home/simon/clone/git/zkfiddle-sandbox/";
    	System.out.println(Settings.get());
		DetectionResults cloneDetection = Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/"));
		tables.reportMetrics("Kryo", cloneDetection.getMetrics());
		try {
			writeStringToFile(new File(SavePaths.getMyOutputFolder()+"refactor.txt"), cloneDetection.getRefactorResults().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @Test
    @Ignore("Requires external repository at /home/simon/clone/git/Alice/")
    public void testMetricTables3() {
    	MetricsTable tables = new MetricsTable();
    	System.out.println("ning-api-java");
    	String path = "/home/simon/clone/git/Alice/";
    	System.out.println(Settings.get());
		tables.reportMetrics("Kryo", Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).getMetrics());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/git/kryo-serializers/")
    public void testRef() {
    	System.out.println("kryo-serializers");
    	String path = "/Users/sbaars/git/kryo-serializers/";
    	System.out.println(Settings.get());
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/kryo-serializers/")
    public void testCustom() {
    	System.out.println("kryo-serializers");
    	String path = "/Users/sbaars/clone/git/kryo-serializers/";
    	System.out.println(Settings.get());
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/athena/")
    public void testAthena() {
    	System.out.println("athena");
    	String path = "/Users/sbaars/clone/git/athena/";
    	System.out.println(Settings.get());
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/Caronas/")
    public void testCaronas() {
    	System.out.println("caronas");
    	String path = "/Users/sbaars/clone/git/Caronas/";
    	System.out.println(Settings.get());
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/abmash/")
    public void testAbmashMethodScope() {
    	Settings.get().setScope(Scope.METHODSONLY);
    	System.out.println("abmash");
    	String path = "/Users/sbaars/clone/git/abmash/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
		Settings.get().setScope(Scope.ALL);
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/abmash/")
    public void testMetricsOutput() {
    	System.out.println("abmash");
    	String path = "/Users/sbaars/clone/git/abmash/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).getMetrics());
		Settings.get().setCloneType(CloneType.TYPE1);
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).getMetrics());
		Settings.get().setCloneType(CloneType.TYPE1R);
    }
    
   /* public void testThread() {
    	System.out.println("custom2");
    	CorpusThread t = new CorpusThread(new File("/Users/sbaars/clone/java_projects/gatein-forge-plugin/src/main/java/"));
    	while(t.isAlive())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
    }*/
    
    /**
     * Test for clones that consist of lines that do not occur elsewhere.
     */
    @Test
    public void testSimpleClones() {
    	System.out.println("testSimpleClones");
    	System.out.println(testProject(SIMPLE_PROJECT));
    }
    
    @Test
    public void testNestedClone() {
    	System.out.println("testNestedClone");
    	System.out.println(testProject("NestedClone"));
    }
    
	/**
     * Test for clones that consist of all equal lines.
     */
    @Test
    public void testEqualLines() {
    	System.out.println("testEqualLines");
    	System.out.println(testProject(EQUAL_LINES_PROJECT));
    }
    
    /**
     * Test for three clones, of which one starts a line later than the others.
     */
    @Test
    public void testPartialClonesLeft() {
    	System.out.println("testPartialClonesLeft");
    	System.out.println(testProject(PARTIAL_CLONES_LEFT));
    }
    
    /**
     * Test for three clones, of which one ends a line later than the others.
     */
    @Test
    public void testPartialLinesRight() {
    	System.out.println("testPartialLinesRight");
    	System.out.println(testProject(PARTIAL_CLONES_RIGHT));
    }

	/**
     * Test for clones in Java enumerations.
     */
    @Test
    public void testEnumClone() {
    	System.out.println("testEnumClone");
    	System.out.println(testProject(ENUM_PROJECT));
    }
    
    /**
     * Test for clones in a single file, with just a single line to separate the clones.
     */
    @Test
    public void testSingleFile() {
    	System.out.println("testSingleFile");
    	System.out.println(testProject(SINGLE_FILE_PROJECT));
    }
    
    @Test
    public void testEqualLinesSingleFile() {
    	System.out.println("testEqualLinesSingleFile");
    	System.out.println(testProject("EqualLinesSingleFile"));
    }
    
    
    /**
     * Test for clones that differ in length but consist of lines with equal tokens.
     */
    @Test
    public void testUnequalSizeClones() {
    	System.out.println("testUnequalSizeClones");
    	System.out.println(testProject(UNEQUAL_SIZE_CLONES_PROJECT));
    }
    
    /**
     * Test for clones that span multiple methods.
     */
    @Test
    public void testSeveralMethodsCloned() {
    	System.out.println("testSeveralMethodsCloned");
    	System.out.println(testProject(SEVERAL_METHODS_PROJECT));
    }
    
    /**
     * Test for clones in import statements.
     */
    @Test
    public void testImportStatements() {
    	System.out.println("testImportStatements");
    	System.out.println(testProject("EqualImportStatements"));
    }
    
    @Test
    public void testEqualLinesDifferentLength() {
    	System.out.println("testEqualLinesDifferentLength");
    	System.out.println(testProject("EqualLinesDifferentLength"));
    }
    
    @Test
    public void testThrowsMethod() {
    	System.out.println("testThrowsMethod");
    	System.out.println(testProject("ThrowsMethod"));
    }
}
