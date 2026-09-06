package com.simonbaars.clonerefactor.types;

import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.helper.Type3Test;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

/**
 * Unit test for the clone detector.
 */
public class Type3Testcases extends Type3Test {
    
    @Test
    @Ignore("Requires external repository at /Users/sbaars/clone/git/AUD/")
    public void testAUD() {
    	Settings.get().setCloneType(CloneType.TYPE3);
    	System.out.println("AUD");
    	String path = "/Users/sbaars/clone/git/AUD/";
		System.out.println(Main.cloneDetection(Paths.get(path), Paths.get(path+"src/main/java/")).sorted());
		Settings.get().setCloneType(CloneType.TYPE3R);
    }
    
    @Test
    public void testStatementAddedLeft() {
    	System.out.println("testStatementAddedLeft");
    	System.out.println(testProject("StatementAddedLeft"));
    }
    
    @Test
    public void testStatementAddedRight() {
    	System.out.println("testStatementAddedRight");
    	System.out.println(testProject("StatementAddedRight"));
    }
    
    @Test
    public void testStatementAddedBothSides() {
    	System.out.println("testStatementAddedBothSides");
    	System.out.println(testProject("StatementAddedBothSides"));
    }
    
    @Test
    public void testSizeThreeCloneClass() {
    	System.out.println("testSizeThreeCloneClass");
    	System.out.println(testProject("SizeThreeCloneClass"));
    }
}
