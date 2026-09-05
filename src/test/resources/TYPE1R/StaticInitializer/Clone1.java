public class Clone1 {
	private static int counter;
	
	static {
		counter = 0;
		System.out.println("Static initializer running");
		for (int i = 0; i < 5; i++) {
			counter += i;
		}
		System.out.println("Counter initialized to: " + counter);
	}
}
