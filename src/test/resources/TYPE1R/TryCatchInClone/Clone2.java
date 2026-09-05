public class Clone2 {
	public void method2() {
		try {
			int x = Integer.parseInt("456");
			int y = x * 2;
			System.out.println("Parsed: " + x);
			System.out.println("Doubled: " + y);
		} catch (NumberFormatException e) {
			System.err.println("Failed to parse");
			System.err.println("Using default value");
		}
	}
}
