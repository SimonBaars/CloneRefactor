public class Clone2 {
	public int method2() {
		try {
			int x = calculate();
			return x * 2;
		} finally {
			cleanup();
		}
	}
	
	private int calculate() { return 20; }
	private void cleanup() { }
}
