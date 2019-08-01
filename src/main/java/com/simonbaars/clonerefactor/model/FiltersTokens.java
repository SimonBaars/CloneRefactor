package com.simonbaars.clonerefactor.model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.javaparser.JavaToken;
import com.github.javaparser.JavaToken.Category;
import com.github.javaparser.TokenRange;
import com.simonbaars.clonerefactor.settings.Settings;

public interface FiltersTokens {
	public static final Category[] NO_TOKEN = {Category.COMMENT, Category.EOL, Category.WHITESPACE_NO_EOL};
	public static final Category[] LITERATURE_TYPE2_NO_TOKEN = {Category.COMMENT, Category.EOL, Category.WHITESPACE_NO_EOL, Category.IDENTIFIER, Category.LITERAL};
	
	public default Stream<JavaToken> getEffectiveTokens(TokenRange tokens) {
		return StreamSupport.stream(tokens.spliterator(), false).filter(this::isComparableToken);
	}
	
	public default Stream<JavaToken> getEffectiveTokens(Optional<TokenRange> tokens) {
		if(!tokens.isPresent())
			return Stream.empty();
		return getEffectiveTokens(tokens.get());
	}
	
	public default List<JavaToken> getEffectiveTokenList(TokenRange tokens){
		return getEffectiveTokens(tokens).collect(Collectors.toList());
	}
	
	public default List<JavaToken> getEffectiveTokenList(Optional<TokenRange> tokens){
		return getEffectiveTokens(tokens).collect(Collectors.toList());
	}
	
	public default boolean isComparableToken(JavaToken t) {
		return isComparableToken(t, NO_TOKEN);
	}
	
	public default boolean isComparableToken(JavaToken t, Category[] catArray) {
		return Arrays.stream(catArray).noneMatch(c -> c.equals(t.getCategory()));
	}
	
	public default List<JavaToken> filterTokensForCompare(List<JavaToken> tokens) {
		return Settings.get().getCloneType().isNotTypeOne() ? tokens.stream().filter(t -> isComparableToken(t, LITERATURE_TYPE2_NO_TOKEN)).collect(Collectors.toList()) : tokens;
	}
}
