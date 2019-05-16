package com.simonbaars.clonerefactor.ast.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.nodeTypes.NodeWithImplements;
import com.simonbaars.clonerefactor.model.FiltersTokens;

public interface DeterminesNodeTokens extends FiltersTokens, DeterminesNodeRange {
	public default List<JavaToken> calculateTokensFromNode(Node n) {
		Range validRange = getValidRange(n);
		List<JavaToken> tokens = new ArrayList<>();
		for(JavaToken token : n.getTokenRange().get()) {
			Optional<Range> r = token.getRange();
			if(r.isPresent()) {
				if(!validRange.contains(r.get())) break;
				if(isComparableToken(token)) tokens.add(token);
				if(n instanceof NodeWithImplements && token.asString().equals("{")) break; // We cannot exclude the body of class files, this is a workaround.
			}
		}
		return tokens;
	}
	
	public default Range getRange(List<JavaToken> tokens) {
		return new Range(tokens.get(0).getRange().get().begin, tokens.get(tokens.size()-1).getRange().get().end);
	}
	
	public default Range getRange(Node n) {
		return getRange(calculateTokensFromNode(n));
	}
}
