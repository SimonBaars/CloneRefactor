public class Clone1 {
	public boolean returnBool()
	{
		if (!!false != !true)
		{
			System.out.println("cloned");
			System.out.println("cloned2");
			System.out.println("cloned3");
			System.out.println("cloned4");
			return false;
		}
		System.out.println("Noclone");
		return false;
	}
}