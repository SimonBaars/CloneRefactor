package com.simonbaars.clonerefactor.helper;

import com.simonbaars.clonerefactor.core.util.DoesFileOperations;
import com.simonbaars.clonerefactor.settings.CloneType;

public class Type1Test extends TypeTest implements DoesFileOperations {	
	public Type1Test( String testName ) {
        super( testName );
    }

	@Override
	protected CloneType getCloneType() {
		return CloneType.TYPE1R;
	}
}
