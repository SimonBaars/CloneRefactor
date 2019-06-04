package com.simonbaars.clonerefactor.helper;

import java.io.File;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.metrics.CloneContentsTest;
import com.simonbaars.clonerefactor.model.DetectionResults;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

import junit.framework.TestCase;

public class Type1Test extends TestCase {
	public Type1Test( String testName ) {
        super( testName );
    }
	
	@Override
    public void setUp() {
    	Settings.get().setCloneType(CloneType.TYPE1);
    	Settings.get().setUseLiteratureTypeDefinitions(false);
    }
	
	protected DetectionResults testProject(String project) {
		return Main.cloneDetection(CloneContentsTest.class.getClassLoader().getResource("Type1"+File.separator+project).getFile());
	}
}
