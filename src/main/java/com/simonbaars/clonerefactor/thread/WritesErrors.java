package com.simonbaars.clonerefactor.thread;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.simonbaars.clonerefactor.util.FileUtils;

public interface WritesErrors {
	public default void writeError(String path, Exception exception) {
		try {
			FileUtils.writeStringToFile(new File(path+".txt"), ExceptionUtils.getFullStackTrace(exception));
		} catch (IOException | NullPointerException e) {
			e.printStackTrace(); // Well, this is awkward...
		}
	}
}
