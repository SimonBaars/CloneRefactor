package com.simonbaars.clonerefactor.detection.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.javaparser.JavaToken;
import com.github.javaparser.JavaToken.Category;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.settings.CloneType;

public interface FiltersTokens {
	public static final Category[] NO_TOKEN = {Category.COMMENT, Category.EOL, Category.WHITESPACE_NO_EOL};
	public static final Category[] LITERATURE_TYPE2_NO_TOKEN = {Category.COMMENT, Category.EOL, Category.WHITESPACE_NO_EOL, Category.IDENTIFIER, Category.LITERAL};
	
	public default Stream<JavaToken> getEffectiveTokens(TokenRange tokens) {
		return StreamSupport.stream(tokens.spliterator(), false).filter(this::isComparableToken);
	}
	
	public default Stream<JavaToken> getEffectiveTokens(Node node) {
		Optional<TokenRange> tokenRange = node.getTokenRange();
		if(!tokenRange.isPresent())
			return Stream.empty();
		return getEffectiveTokens(tokenRange.get());
	}
	
	public default List<JavaToken> getEffectiveTokenList(TokenRange tokens){
		return getEffectiveTokens(tokens).collect(Collectors.toList());
	}
	
	public default List<JavaToken> getEffectiveTokenList(Node node){
		return getEffectiveTokens(node).collect(Collectors.toList());
	}
	
	public default boolean isComparableToken(JavaToken t) {
		return isComparableToken(t, NO_TOKEN);
	}
	
	public default int countTokens(Node n) {
		return Math.toIntExact(getEffectiveTokens(n).count());
	}
	
	public default boolean isComparableToken(JavaToken t, Category[] catArray) {
		return Arrays.stream(catArray).noneMatch(c -> c.equals(t.getCategory()));
	}
	
	public default List<JavaToken> filterTokensForCompare(CloneType type, List<JavaToken> tokens) {
		return type.isNotType1() ? tokens.stream().filter(t -> isComparableToken(t, LITERATURE_TYPE2_NO_TOKEN)).collect(Collectors.toList()) : tokens;
	}
}
