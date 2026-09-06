public enum Clone1 {
	VALUE1(10),
	VALUE2(20),
	VALUE3(30);
	
	private final int code;
	
	Clone1(int code) {
		this.code = code;
		System.out.println("Enum value: " + code);
	}
	
	public int getCode() {
		return code;
	}
}
