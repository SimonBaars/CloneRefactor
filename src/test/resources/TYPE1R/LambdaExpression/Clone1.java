import java.util.Arrays;
import java.util.List;

public class Clone1 {
	public void method1() {
		List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
		numbers.forEach(n -> {
			int squared = n * n;
			System.out.println("Square: " + squared);
		});
	}
}
