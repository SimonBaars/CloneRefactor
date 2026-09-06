package com.simonbaars.clonerefactor.helper;

import org.junit.Before;

import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class Type2Test extends TypeTest {
	@Override
	protected CloneType getCloneType() {
		return CloneType.TYPE2R;
	}
	
	@Override
	@Before
    public void setUp() {
		super.setUp();
    	Settings.get().setType2VariabilityPercentage(5);
    }
}
