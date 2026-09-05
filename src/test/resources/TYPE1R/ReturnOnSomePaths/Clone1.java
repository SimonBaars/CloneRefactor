public class Clone1 {
	public void method1(int x) {
		System.out.println("Processing: " + x);
		if (x > 10) {
			System.out.println("Large value");
			return;
		}
		System.out.println("Small value: " + x);
		System.out.println("Continuing processing");
		System.out.println("More work");
	}
}
