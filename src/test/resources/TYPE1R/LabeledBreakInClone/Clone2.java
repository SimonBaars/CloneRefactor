public class Clone2 {
	public void method2() {
		outer: for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				if (j == 5) {
					break outer;
				}
				System.out.println("Processing " + i + "," + j);
			}
		}
	}
}
