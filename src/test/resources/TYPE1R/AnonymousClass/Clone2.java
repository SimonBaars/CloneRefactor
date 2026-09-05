public class Clone2 {
	public void method2() {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				System.out.println("Task running");
				System.out.println("Task complete");
			}
		};
		task.run();
	}
}
