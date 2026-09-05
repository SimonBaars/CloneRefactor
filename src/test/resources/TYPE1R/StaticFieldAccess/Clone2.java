public class Clone2 {
	private static int globalCounter = 0;
	
	public void method2() {
		globalCounter++;
		System.out.println("Global counter: " + globalCounter);
		System.out.println("Processing with counter: " + globalCounter);
	}
}
