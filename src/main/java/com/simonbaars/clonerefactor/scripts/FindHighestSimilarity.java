package com.simonbaars.clonerefactor.scripts;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import com.simonbaars.clonerefactor.core.util.DoesFileOperations;
import com.simonbaars.clonerefactor.core.util.SavePaths;

public class FindHighestSimilarity implements DoesFileOperations {

	public static void main(String[] args) throws IOException {
		new FindHighestSimilarity().run();
	}

	private void run() throws IOException {
		File[] contents = new File(SavePaths.getOutputFolder()).listFiles();
		double highest = 0;
		File highestFile = null;
		for(File file : contents) {
			if(file.listFiles()!=null) {
				Optional<File> res = Arrays.stream(file.listFiles()).filter(e -> e.getName().equals("res.txt")).findAny();
				if(res.isPresent()) {
					String similarityLine = getFileAsString(res.get()).split(System.lineSeparator())[1];
					System.out.println(similarityLine+" in "+res.get() +" "+ getFileAsString(res.get()).split(System.lineSeparator())[0]);
					double d = Double.parseDouble(similarityLine.substring(similarityLine.lastIndexOf(' ')+1));
					if(d > highest) {
						highest = d;
						highestFile = res.get();
					}
				}
			}
		}
		System.out.println("Highest "+highest+" in file "+highestFile);
	}

}
