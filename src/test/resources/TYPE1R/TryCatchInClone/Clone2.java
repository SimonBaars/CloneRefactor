public class Clone2 {
	public void method2() {
		try {
			int x = Integer.parseInt("456");
			System.out.println("Parsed: " + x);
		} catch (NumberFormatException e) {
			System.err.println("Failed to parse");
		}
	}
}
