public class Clone1 {
	public int method1() {
		try {
			int x = calculate();
			return x * 2;
		} finally {
			cleanup();
		}
	}
	
	private int calculate() { return 10; }
	private void cleanup() { }
}
