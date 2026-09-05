public class Clone1 {
	public void method1() {
		try {
			int x = Integer.parseInt("123");
			System.out.println("Parsed: " + x);
		} catch (NumberFormatException e) {
			System.err.println("Failed to parse");
		}
	}
}
