public class Clone1 {
	public void method1() {
		int outerVar = 10;
		for (int i = 0; i < 5; i++) {
			int computed = outerVar + i;
			int doubled = computed * 2;
			System.out.println("Computed: " + computed);
			System.out.println("Doubled: " + doubled);
			System.out.println("Outer: " + outerVar);
		}
	}
}
