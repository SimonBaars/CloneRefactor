public class Clone1 {
	private final Object lock = new Object();
	
	public void method1() {
		synchronized (lock) {
			int x = 10;
			System.out.println("Processing: " + x);
		}
	}
}
