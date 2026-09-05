public class Clone1 {
	public void method1() {
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
