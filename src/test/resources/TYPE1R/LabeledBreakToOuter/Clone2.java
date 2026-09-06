public class Clone2 {
	public void method2() {
		outer: for (int i = 0; i < 20; i++) {
			// Clone starts here - break to outer label outside clone
			if (i == 5) {
				break outer;
			}
			System.out.println("Value: " + i);
		}
	}
}
