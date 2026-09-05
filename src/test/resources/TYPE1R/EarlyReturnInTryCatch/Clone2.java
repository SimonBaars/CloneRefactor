public class Clone2 {
	public int method2(String s) {
		try {
			int x = Integer.parseInt(s);
			if (x < 0) {
				return -1;
			}
			return x * 2;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
