import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Clone1 {

	public static void main(String[] args) {
		System.out.println("I'm a clone1");
		System.out.println("I'm a clone2");
		System.out.println("I'm a clone3");
		System.out.println("I'm a clone4");
		System.out.println("I'm a clone5");
		System.out.println("I'm a clone6");
		Stream.of("I'm", "part", "of", "a", "type", "3", "clone").collect(Collectors.joining(" "));
		System.out.println("I'm a clone7");
		System.out.println("I'm a clone8");
		System.out.println("I'm a clone9");
		System.out.println("I'm a clone10");
		System.out.println("I'm a clone11");
		System.out.println("I'm a clone12");
	}

}
