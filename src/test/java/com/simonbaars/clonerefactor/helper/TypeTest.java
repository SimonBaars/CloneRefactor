package com.simonbaars.clonerefactor.helper;

import java.io.File;

import com.simonbaars.clonerefactor.Main;
import com.simonbaars.clonerefactor.detection.model.DetectionResults;
import com.simonbaars.clonerefactor.metrics.CloneContentsTest;
import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

import junit.framework.TestCase;

public abstract class TypeTest extends TestCase {

	public TypeTest() {}

	public TypeTest(String name) {
		super(name);
	}

	protected abstract CloneType getCloneType();
	
	@Override
    public void setUp() {
    	Settings.get().setCloneType(getCloneType());
    }
	
	protected DetectionResults testProject(String project) {
		return Main.cloneDetection(CloneContentsTest.class.getClassLoader()
				.getResource(getCloneType().name()).getFile()+File.separator+project).sorted();
	}
}
