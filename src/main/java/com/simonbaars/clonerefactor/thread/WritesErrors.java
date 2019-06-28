package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.simonbaars.clonerefactor.util.FileUtils;
import com.simonbaars.clonerefactor.util.SavePaths;

public interface WritesErrors {
	public default void writeError(String path, Exception exception) {
		try {
			try {
				FileUtils.writeStringToFile(new File(path+".txt"), ExceptionUtils.getStackTrace(exception));
			} catch (NullPointerException e) {
				FileUtils.writeStringToFile(new File(path+".txt"), ExceptionUtils.getStackTrace(new IllegalStateException("No exception present")));
			}
		} catch (IOException | NullPointerException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, "Something went wrong while writing an error.", e);
		}
	}
	
	public default void writeProjectError(String name, Exception exception) {
		writeError(SavePaths.createDirectoryIfNotExists(SavePaths.getErrorFolder())+name, exception);
	}
}
