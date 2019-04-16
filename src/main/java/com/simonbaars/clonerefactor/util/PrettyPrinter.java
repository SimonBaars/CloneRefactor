package com.simonbaars.clonerefactor.util;

import java.util.Arrays;
import java.util.List;

import com.simonbaars.clonerefactor.model.Sequence;

public class PrettyPrinter {
	public static String prettify(List<Sequence> clones) {
		return Arrays.toString(clones.toArray()).replace("Location [", "\nLocation [").replace("Sequence [sequence=[", "\nSequence [sequence=[");
	}
}
