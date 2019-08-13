package clonegraph.compare;

import java.util.Arrays;
import java.util.List;

import com.github.javaparser.JavaToken;
import com.github.javaparser.Range;

public class CompareTokens extends Compare {
	
	private final List<JavaToken> tokens;
	
	public CompareTokens(List<JavaToken> tokens, Range r) {
		super(r);
		this.tokens = tokens;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o) && tokens.equals(((CompareTokens)o).tokens);
	}

	@Override
	public int hashCode() {
		return tokens.hashCode();
	}

	@Override
	public String toString() {
		return "CompareToken [tokens=" + Arrays.toString(tokens.toArray()) + "]";
	}
}
