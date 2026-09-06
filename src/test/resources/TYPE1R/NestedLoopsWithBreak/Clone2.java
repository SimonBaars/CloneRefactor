public class Clone2 {
	public void method2() {
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				if (j == 5) {
					break;
				}
				System.out.println("i=" + i + ", j=" + j);
			}
		}
	}
}
