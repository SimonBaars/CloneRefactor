package com.simonbaars.clonerefactor.detection;

import com.github.javaparser.ast.Node;

/**
 * Placeholder class for more complex comparisons whether nodes are equal
 * @author sbaars
 *
 */
public class CompareNodes {
	public static boolean nodesEqual(Node n1, Node n2) {
		return n1.equals(n2);
	}
}
