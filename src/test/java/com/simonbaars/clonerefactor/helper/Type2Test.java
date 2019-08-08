package com.simonbaars.clonerefactor.helper;

import com.simonbaars.clonerefactor.settings.CloneType;
import com.simonbaars.clonerefactor.settings.Settings;

public class Type2Test extends TypeTest {	
	public Type2Test( String testName ) {
        super( testName );
    }

	@Override
	protected CloneType getCloneType() {
		return CloneType.TYPE2R;
	}
	
	@Override
    public void setUp() {
		super.setUp();
    	Settings.get().setType2VariabilityPercentage(5);
    }
}
