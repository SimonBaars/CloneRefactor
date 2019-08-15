
public class Clone1 {

	public static void main(String[] args) {
		Map<DummyEnum, Object> enumMap = new EnumMap<DummyEnum, Object>(DummyEnum.class);
        for (DummyEnum e : DummyEnum.values()) {
            enumMap.put(e, o1);
        }
	}

	private enum DummyEnum {
        VALUE1, VALUE2
    }
}
