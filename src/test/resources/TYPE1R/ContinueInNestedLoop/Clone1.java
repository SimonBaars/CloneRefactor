public class Clone1 {
	public void method1() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (j % 2 == 0) {
					continue;
				}
				System.out.println("Odd j: " + j);
			}
		}
	}
}
