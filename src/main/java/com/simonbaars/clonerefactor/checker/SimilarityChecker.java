package com.simonbaars.clonerefactor.checker;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.datatype.FlattenedList;
import com.simonbaars.clonerefactor.model.SimilarityReg;

public class SimilarityChecker {
	private static double similarity(List<List<Node>> leftClone, List<List<Node>> rightClone) {
		final List<Node> left = new FlattenedList<>(leftClone);
		final List<Node> right = new FlattenedList<>(rightClone);
		SimilarityReg r = new SimilarityReg();
		final int leftSize = left.size();
		final int rightSize = right.size();
		for(int i = 0; i<Math.max(leftSize, rightSize); i++) {
			Node leftLine = i<leftSize ? left.get(i) : null;
			Node rightLine = i<rightSize ? right.get(i) : null;
			boolean checkEqual = true;
			if(currentToken(r, i, leftLine, rightLine, true)) {
				r.incrementSame(1);
				r.incrementDifferent(1);
				checkEqual = false;
			}
			if(currentToken(r, i, leftLine, rightLine, false)) {
				r.incrementSame(1);
				if(checkEqual)
					r.incrementDifferent(1);
				else r.decementDifferent();
				checkEqual = false;
			}
			if(checkEqual) {
				System.out.println("Compare "+leftLine+" vs "+rightLine+" = "+(leftLine == rightLine));
				if(leftLine == rightLine){
					r.incrementSame(2);
				} else {
					r.incrementDifferent(2); //Add 2 because on both sides (left and right) a different is found.
					r.putLeftBuff(i, leftLine);
					r.putRightBuff(i, rightLine);
				}
			}
		}
		System.out.println("Same = "+r.getSame()+", Different = "+r.getDifferent()+", DiffPoints = "+r.getDiffPoints()+", percSame = "+(r.getSame()/((double)(r.getSame()+r.getDifferent()))*100D));
		return r.getSame()/((double)(r.getSame()+r.getDifferent()))*100D;
	}

	private static boolean currentToken(SimilarityReg r, int i, Node leftLine, Node rightLine, boolean isLeft) {
		Map<Integer,Node> thisMap = isLeft ? r.getRightBuff() : r.getLeftBuff();
		Node relevantLine = isLeft ? leftLine : rightLine;
		if(relevantLine == null)
			return false;
		for(Entry<Integer, Node> e : thisMap.entrySet()) { // TODO: This can be replaced by map access for O(n) performance.
			if(e.getValue().equals(relevantLine)) {
				r.incrementDiffPoints(i-e.getKey());
				thisMap.remove(e.getKey());
				if(isLeft) {
					r.putRightBuff(i, rightLine);
				} else {
					r.putLeftBuff(i, leftLine);
				}
				return true;
			}
		}
		return false;
	}
}
