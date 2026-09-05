public class Clone2 {
	public void method2() {
		int outerVar = 20;
		for (int i = 0; i < 10; i++) {
			int computed = outerVar + i;
			int doubled = computed * 2;
			System.out.println("Computed: " + computed);
			System.out.println("Doubled: " + doubled);
			System.out.println("Outer: " + outerVar);
		}
	}
}
