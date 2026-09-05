public class Clone2 {
	public int method() {
		int y = 20;
		if (y > 5) {
			System.out.println("cloned1");
			System.out.println("cloned2");
			return y * 2;
		}
		System.out.println("cloned3");
		System.out.println("cloned4");
		return y;
	}
}
