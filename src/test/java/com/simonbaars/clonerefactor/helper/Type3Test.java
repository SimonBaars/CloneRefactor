package com.simonbaars.clonerefactor.helper;

import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class Type3Test extends TypeTest {	
	public Type3Test( String testName ) {
        super( testName );
    }

	@Override
	protected CloneType getCloneType() {
		return CloneType.TYPE3;
	}
	
	@Override
    public void setUp() {
    	super.setUp();
    	Settings.get().setType2VariabilityPercentage(5);
    	Settings.get().setType3GapSize(20.0);
    }
}
