public class Clone1 {
	public int method1(int x) {
		int result;
		if (x > 0) {
			result = x * 2;
			System.out.println("Positive: " + result);
			return result;
		} else {
			result = x * 3;
			System.out.println("Non-positive: " + result);
			return result;
		}
	}
}
