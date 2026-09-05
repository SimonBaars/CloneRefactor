package com.simonbaars.clonerefactor.types;

import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.helper.Type2Test;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;
import com.simonbaars.clonerefactor.thread.CalculatesTimeIntervals;

/**
 * Unit test for the clone detector.
 */
public class Type2Testcases extends Type2Test implements CalculatesTimeIntervals {
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/SolrMQ/")
    public void testSolrMQ() {
    	System.out.println("custom");
        String path = "/Users/sbaars/clone/git/SolrMQ/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/Caronas/")
    public void testCaronas() {
    	Settings.get().setCloneType(CloneType.TYPE2);
    	System.out.println("caronas");
    	String path = "/Users/sbaars/clone/git/Caronas/";
    	System.out.println(Settings.get());
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
		Settings.get().setCloneType(CloneType.TYPE2R);
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/SimpleHTTPServer/")
    public void testSimpleHTTPServer() {
    	System.out.println("SimpleHTTPServer");
        String path = "/Users/sbaars/clone/git/SimpleHTTPServer/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/Wykop.pl-Java-SDK/")
    public void testWykopplJavaSDK() {
    	System.out.println("Wykop.pl-Java-SDK");
        String path = "/Users/sbaars/clone/git/Wykop.pl-Java-SDK/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/cotopaxi-core/")
    public void testCotopaxiCore() {
    	System.out.println("cotopaxi-core");
    	String path = "/Users/sbaars/clone/git/cotopaxi-core/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/abmash/")
    public void testAbmash() {
    	System.out.println("abmash");
    	String path = "/Users/sbaars/clone/git/abmash/";
    	long t = System.currentTimeMillis();
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
		System.out.println("Time 2R: "+interval(t));
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/abmash/")
    public void testAbmashLiterature() {
    	Settings.get().setCloneType(CloneType.TYPE2);
    	System.out.println("abmash");
    	String path = "/Users/sbaars/clone/git/abmash/";
    	long t = System.nanoTime();
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
		System.out.println("Time T2: "+(System.nanoTime()-t));
		Settings.get().setCloneType(CloneType.TYPE2R);
    }
    
    @Test
    public void testDifferentLiterals() {
    	System.out.println("testDifferentLiterals");
    	System.out.println(testProject("DifferentLiterals"));
    }
    
    @Test
    public void testDifferentMethods() {
    	System.out.println("testDifferentMethods");
    	System.out.println(testProject("DifferentMethods"));
    }
    
    @Test
    public void testHighVariability() {
    	System.out.println("testHighVariability");
    	System.out.println(testProject("HighVariability"));
    }
    
    @Test
    public void testHighVariabilityInstance() {
    	System.out.println("testHighVariabilityInstance");
    	System.out.println(testProject("HighVariabilityInstance"));
    }
    
    @Test
    public void testThresholds() {
    	Settings.get().setType2VariabilityPercentage(100);
    	System.out.println("testHighVariabilityInstance");
    	System.out.println(testProject("HighVariabilityInstance"));
    	Settings.get().setType2VariabilityPercentage(5);
    }
    
    @Test
    public void testThreeDifferent() {
    	System.out.println("testThreeDifferent");
    	System.out.println(testProject("ThreeDifferent"));
    }
    
    @Test
    public void testSingle() {
    	System.out.println("testSingle");
    	System.out.println(testProject("Single"));
    }
    
    @Test
    public void testPartCloned() {
    	System.out.println("testPartCloned");
    	System.out.println(testProject("PartCloned"));
    }
}
