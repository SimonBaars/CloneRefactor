package com.simonbaars.clonerefactor;

import com.simonbaars.clonerefactor.settings.Settings;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestSettings extends TestCase{
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TestSettings( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( TestSettings.class );
    }
    
    public void testSettingsFile() {
    	assertNotNull(Settings.get().getCloneType());
    }
}
