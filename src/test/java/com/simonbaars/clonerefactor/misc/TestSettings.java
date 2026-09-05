package com.simonbaars.clonerefactor.misc;

import org.junit.Assert;
import org.junit.Test;

import com.simonbaars.clonerefactor.settings.Settings;

public class TestSettings {
    
    @Test
    public void testSettingsFile() {
    	Assert.assertNotNull(Settings.get().getCloneType());
    }
}
