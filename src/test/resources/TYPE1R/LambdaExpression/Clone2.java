import java.util.Arrays;
import java.util.List;

public class Clone2 {
	public void method2() {
		List<Integer> numbers = Arrays.asList(10, 20, 30, 40, 50);
		numbers.forEach(n -> {
			int squared = n * n;
			System.out.println("Square: " + squared);
		});
	}
}
