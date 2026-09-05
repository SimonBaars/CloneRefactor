package com.simonbaars.clonerefactor.helper;

import org.junit.Before;

import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class Type3Test extends TypeTest {
	@Override
	protected CloneType getCloneType() {
		return CloneType.TYPE3R;
	}
	
	@Override
	@Before
    public void setUp() {
    	super.setUp();
    	Settings.get().setType2VariabilityPercentage(5);
    	Settings.get().setType3GapSize(20.0);
    }
}
