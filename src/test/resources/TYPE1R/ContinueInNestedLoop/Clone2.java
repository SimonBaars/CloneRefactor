public class Clone2 {
	public void method2() {
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				if (j % 2 == 0) {
					continue;
				}
				System.out.println("Odd j: " + j);
			}
		}
	}
}
