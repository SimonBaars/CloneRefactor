public class Clone2 {
	private int value = 20;
	
	public void method2() {
		int localValue = this.value * 2;
		System.out.println("This value: " + this.value);
		System.out.println("Computed: " + localValue);
	}
}
