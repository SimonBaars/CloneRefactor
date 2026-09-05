public class Clone2 extends BaseClass {
	public void method2() {
		super.initialize();
		System.out.println("Initialized from clone 2");
		super.finalize();
	}
}

class BaseClass {
	public void initialize() { }
	public void finalize() { }
}
