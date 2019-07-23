package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.simonbaars.clonerefactor.util.DoesFileOperations;
import com.simonbaars.clonerefactor.util.SavePaths;

public interface WritesErrors extends DoesFileOperations {
	public default void writeError(String path, Exception exception) {
		try {
			tryToWriteError(path, exception);
		} catch (IOException | NullPointerException e) {
			Logger.getAnonymousLogger().log(Level.WARNING, "Something went wrong while writing an error.", e);
		}
	}

	public default void tryToWriteError(String path, Exception exception) throws IOException {
		try {
			writeStringToFile(new File(path+".txt"), exceptionToString(exception));
		} catch (NullPointerException e) {
			writeStringToFile(new File(path+".txt"), exceptionToString(new IllegalStateException("No exception present")));
		}
	}
	
	public default String exceptionToString(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	public default void writeProjectError(String name, Exception exception) {
		writeError(SavePaths.createDirectoryIfNotExists(SavePaths.getErrorFolder())+name, exception);
	}
}
