public class Clone1 {
	private static int globalCounter = 0;
	
	public void method1() {
		globalCounter++;
		System.out.println("Global counter: " + globalCounter);
		System.out.println("Processing with counter: " + globalCounter);
	}
}
