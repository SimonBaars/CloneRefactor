public class Clone1 {
	private int value;
	private String name;
	
	public Clone1(int value, String name) {
		this.value = value * 2;
		this.name = name.toUpperCase();
		System.out.println("Initializing with: " + this.value);
		System.out.println("Name: " + this.name);
	}
}
