package com.simonbaars.clonerefactor.helper;

import java.io.File;

import org.junit.Before;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.metrics.CloneContentsTest;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public abstract class TypeTest {

	protected abstract CloneType getCloneType();
	
	@Before
    public void setUp() {
    	Settings.get().setCloneType(getCloneType());
    }
	
	protected DetectionResults testProject(String project) {
		return Main.cloneDetection(CloneContentsTest.class.getClassLoader()
				.getResource(getCloneType().name()).getFile()+File.separator+project).sorted();
	}
}
