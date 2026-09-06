public class Clone1 {
	public int method() {
		int x = 10;
		if (x > 5) {
			System.out.println("cloned1");
			System.out.println("cloned2");
			return x * 2;
		}
		System.out.println("cloned3");
		System.out.println("cloned4");
		return x;
	}
}
