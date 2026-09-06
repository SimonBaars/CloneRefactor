package com.simonbaars.clonerefactor.misc;

import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import com.simonbaars.clonerefactor.Main;

/**
 * Here we use libraries to test projects.
 */
public class LibTest {
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/json-collection/")
    public void testJSONCollection() {
    	System.out.println("json-collection");
        String path = "/Users/sbaars/clone/git/json-collection/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/smslib/")
    public void testSMSLib() {
    	System.out.println("smslib");
        String path = "/Users/sbaars/clone/git/smslib/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/spark/")
    public void testSpark() {
    	System.out.println("spark");
        String path = "/Users/sbaars/clone/git/spark/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/pa/")
    public void testPA() {
    	System.out.println("pa");
        String path = "/Users/sbaars/clone/git/pa/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/atan/")
    public void testAtan() {
    	System.out.println("atan");
        String path = "/Users/sbaars/clone/git/atan/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
    }
}
