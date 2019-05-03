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
	
	public default Stream<JavaToken> getEffectiveTokens(List<JavaToken> tokens) {
		return tokens.stream().filter(e -> Arrays.stream(NO_TOKEN).noneMatch(c -> c.equals(e.getCategory())));
	}
	
	public default Stream<JavaToken> getEffectiveTokens(TokenRange tokens) {
		return StreamSupport.stream(tokens.spliterator(), false).filter(e -> Arrays.stream(NO_TOKEN).noneMatch(c -> c.equals(e.getCategory())));
	}
	
	public default List<JavaToken> getEffectiveTokenList(TokenRange tokens){
		return getEffectiveTokens(tokens).collect(Collectors.toList());
	}
	
	public default List<JavaToken> getEffectiveTokenList(List<JavaToken> tokens){
		return getEffectiveTokens(tokens).collect(Collectors.toList());
	}
}
