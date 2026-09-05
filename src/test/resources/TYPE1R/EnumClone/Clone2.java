public enum Clone2 {
	ALPHA(100),
	BETA(200),
	GAMMA(300);
	
	private final int code;
	
	Clone2(int code) {
		this.code = code;
		System.out.println("Enum value: " + code);
	}
	
	public int getCode() {
		return code;
	}
}
