public class Clone1 {
	private int value = 10;
	
	public void method1() {
		int localValue = this.value * 2;
		System.out.println("This value: " + this.value);
		System.out.println("Computed: " + localValue);
	}
}
