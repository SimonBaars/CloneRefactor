public class Clone1 {
	public void method1() {
		try {
			int x = Integer.parseInt("123");
			int y = x * 2;
			System.out.println("Parsed: " + x);
			System.out.println("Doubled: " + y);
		} catch (NumberFormatException e) {
			System.err.println("Failed to parse");
			System.err.println("Using default value");
		}
	}
}
