public class Clone1 {
	public void method1() {
		outer: for (int i = 0; i < 10; i++) {
			// Clone starts here - break to outer label outside clone
			if (i == 5) {
				break outer;
			}
			System.out.println("Value: " + i);
		}
	}
}
