package com.simonbaars.clonerefactor.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.javaparser.JavaToken;
import com.github.javaparser.JavaToken.Category;
import com.github.javaparser.TokenRange;

public interface FiltersTokens {
	public static final Category[] NO_TOKEN = {Category.COMMENT, Category.EOL, Category.WHITESPACE_NO_EOL};
	
	public default Stream<JavaToken> getEffectiveTokens(TokenRange tokens) {
		return StreamSupport.stream(tokens.spliterator(), false).filter(e -> isComparableToken(e));
	}
	
	public default List<JavaToken> getEffectiveTokenList(TokenRange tokens){
		return getEffectiveTokens(tokens).collect(Collectors.toList());
	}
	
	public default boolean isComparableToken(JavaToken t) {
		return Arrays.stream(NO_TOKEN).noneMatch(c -> c.equals(t.getCategory()));
	}
}
