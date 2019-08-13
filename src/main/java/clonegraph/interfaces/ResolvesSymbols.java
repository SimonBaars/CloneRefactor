package clonegraph.interfaces;

import java.util.Optional;
import java.util.function.Supplier;


public interface ResolvesSymbols {
	public default<T> Optional<T> resolve(Supplier<T> function) {
		try {
			return Optional.of(function.get());
		} catch (Exception e) { // Anything can go wrong resolving symbols :(
			return Optional.empty();
		}
	}
}
