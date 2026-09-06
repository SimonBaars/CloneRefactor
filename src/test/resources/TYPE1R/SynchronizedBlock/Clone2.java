public class Clone2 {
	private final Object lock = new Object();
	
	public void method2() {
		synchronized (lock) {
			int x = 20;
			System.out.println("Processing: " + x);
		}
	}
}
