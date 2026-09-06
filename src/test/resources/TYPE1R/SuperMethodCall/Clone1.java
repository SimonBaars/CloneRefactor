public class Clone1 extends BaseClass {
	public void method1() {
		super.initialize();
		System.out.println("Initialized from clone 1");
		super.finalize();
	}
}

class BaseClass {
	public void initialize() { }
	public void finalize() { }
}
